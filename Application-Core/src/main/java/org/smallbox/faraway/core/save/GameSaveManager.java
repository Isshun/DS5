package org.smallbox.faraway.core.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.game.GameTime;
import org.smallbox.faraway.game.character.CharacterModule;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.GameException;
import org.smallbox.faraway.util.log.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.time.temporal.ChronoUnit.SECONDS;

@ApplicationObject
public class GameSaveManager {
    @Inject private GameInfoFactory gameInfoFactory;
    @Inject private SQLManager sqlManager;
    @Inject private CharacterModule characterModule;
    @Inject private DependencyManager dependencyManager;
    @Inject private GameTime gameTime;
    @Inject private DataManager dataManager;
    @Inject private Game game;

    public void saveGame(GameSaveType type) {
        long time = System.currentTimeMillis();

        Application.notify(observer -> observer.onCustomEvent("save_game.begin", null));

        Date date = new Date();
        String prefixName = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(date);
        File gameDirectory = FileUtils.getSaveDirectory(game.getInfo().name);

        createGameInfoFile(gameDirectory, prefixName, type, date);
        createDatabaseFile(gameDirectory, prefixName);

        sqlManager.post(db -> {
            Gdx.app.postRunnable(() -> {
                try {
                    createScreenshot(gameDirectory, prefixName);
                    createArchive(gameDirectory, prefixName);
                    deleteFiles(gameDirectory, prefixName);

                    Log.info("Save game completed (" + (System.currentTimeMillis() - time) + "ms)");
                    Application.notify(observer -> observer.onCustomEvent("save_game.complete", null));
                } catch (IOException | ArchiveException e) {
                    throw new GameException(GameSaveManager.class, e, "Error during game save");
                }
            });
        });
    }

    private void createScreenshot(File gameDirectory, String prefixName) {
        File saveInfoFile = new File(gameDirectory, prefixName + ".png");

        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);

        // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
        for (int i = 4; i < pixels.length; i += 4) {
            pixels[i - 1] = (byte) 255;
        }

        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(new FileHandle(saveInfoFile), pixmap);
        pixmap.dispose();
    }

    private void createGameInfoFile(File gameDirectory, String prefixName, GameSaveType type, Date date) {
        File saveInfoFile = new File(gameDirectory, prefixName + ".json");
        File gameInfoFile = new File(gameDirectory, "game.json");

        GameSaveInfo saveInfo = new GameSaveInfo();
        saveInfo.type = type;
        saveInfo.date = date;
        saveInfo.label = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(saveInfo.date);
        saveInfo.filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(saveInfo.date);
        saveInfo.crew = characterModule.getCount();
        saveInfo.duration = gameTime.getStartGameTime().until(gameTime.now(), SECONDS);
        game.getInfo().saveFiles.add(saveInfo);

        // Write game.json
        try {
            FileUtils.write(saveInfoFile, saveInfo.toJSON().toString(4));
            FileUtils.write(gameInfoFile, gameInfoFactory.toJSON(game.getInfo()).toString(4));
        } catch (IOException e) {
            throw new GameException(GameSaveManager.class, e, "Unable to write game meta info");
        }
    }

    // Create and populate DB
    private void createDatabaseFile(File gameDirectory, String prefixName) {
        File dbFile = new File(gameDirectory, prefixName + ".db");
        sqlManager.openDB(dbFile);

        // Call modules serializers
        dependencyManager.getSubTypesOf(GameSerializer.class).forEach(gameSerializer -> gameSerializer.save(sqlManager));

        sqlManager.closeDB();
    }

    // Delete DB file
    private void deleteFiles(File gameDirectory, String prefixName) throws IOException {
        deleteFile(new File(gameDirectory, prefixName + ".json"));
        deleteFile(new File(gameDirectory, prefixName + ".db"));
        deleteFile(new File(gameDirectory, prefixName + ".png"));
    }

    private void deleteFile(File fileToDelete) throws IOException {
        if (!fileToDelete.delete()) {
            throw new IOException("Unable to delete file: " + fileToDelete.getAbsolutePath());
        }
    }

    // Create archive file
    private void createArchive(File gameDirectory, String prefixName) throws IOException, ArchiveException {
        File archiveFile = new File(gameDirectory, prefixName + ".zip");
        try (OutputStream archiveStream = new FileOutputStream(archiveFile)) {
            try (ArchiveOutputStream archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, archiveStream)) {
                createArchiveEntry(archive, gameDirectory, prefixName + ".db");
                createArchiveEntry(archive, gameDirectory, prefixName + ".png");
                createArchiveEntry(archive, gameDirectory, prefixName + ".json");
                archive.finish();
            }
        }
    }

    private void createArchiveEntry(ArchiveOutputStream archive, File gameDirectory, String fileName) throws IOException {
        File file = new File(gameDirectory, fileName);
        archive.putArchiveEntry(new ZipArchiveEntry(fileName));
        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
            IOUtils.copy(input, archive);
        }
        archive.closeArchiveEntry();
    }
}