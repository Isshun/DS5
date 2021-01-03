package org.smallbox.faraway.core.game;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationObject
public class GameSaveManager {
    
    @Inject
    private SQLManager sqlManager;

    @Inject
    private Data data;

    @Inject
    private Game game;

    public interface GameSerializerInterface {
        void onSerializerComplete();
    }

    public List<String> getSaves() {
        File gameSaveDirectory = FileUtils.getSaveDirectory(game.getInfo().name);

        return Stream.of(gameSaveDirectory.listFiles()).map(File::getName).collect(Collectors.toList());
    }

    public void load(Game game, File gameDirectory, String filename, GameSerializerInterface listener) {
        Application.notify(observer -> observer.onCustomEvent("load_game.begin", null));
        long time = System.currentTimeMillis();

        try {
            File archiveFile = new File(gameDirectory, filename + ".zip");

            // Extract ZIP to sqlite DB file
            try (InputStream archiveStream = new FileInputStream(archiveFile)) {
                try (ArchiveInputStream archive = new ArchiveStreamFactory().createArchiveInputStream(ArchiveStreamFactory.ZIP, archiveStream)) {
                    ArchiveEntry entry;
                    while ((entry = archive.getNextEntry()) != null) {
                        try (FileOutputStream fos = new FileOutputStream(new File(gameDirectory, entry.getName()))) {
                            IOUtils.copy(archive, fos);
                        }
                    }
                }
            }
            Log.info("Extract zip: " + (System.currentTimeMillis() - time));

            // Load sqlite DB file
            File dbFile = new File(gameDirectory, filename + ".db");
            sqlManager.openDB(dbFile);

            // Call modules serializers
            DependencyInjector.getInstance().getSubTypesOf(GameSerializer.class).stream()
                    .sorted(Comparator.comparingInt(GameSerializer::getModulePriority))
                    .forEach(serializer -> serializer.load(sqlManager));

            sqlManager.closeDB();

            sqlManager.post(db -> {
                System.gc();
                listener.onSerializerComplete();
                // TODO
//                dbFile.delete();
                Log.info("Load onSave game: " + (System.currentTimeMillis() - time));
                Application.notify(observer -> observer.onCustomEvent("load_game.complete", null));
            });
        } catch (IOException | ArchiveException e) {
            Log.error(e);
        }
    }

    public void saveGame(Game game, GameInfo gameInfo, GameInfo.Type type) {
        Date date = new Date();
        String filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(date);
        File gameDirectory = FileUtils.getSaveDirectory(gameInfo.name);

        GameInfo.GameSaveInfo saveInfo = new GameInfo.GameSaveInfo();
        saveInfo.game = gameInfo;
        saveInfo.type = type;
        saveInfo.date = date;
        saveInfo.label = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(saveInfo.date);
        saveInfo.filename = new SimpleDateFormat("yyyy-MM-dd-hh-hh-mm-ss").format(saveInfo.date);
        gameInfo.saveFiles.add(saveInfo);

        // Write game.json
        try {
            FileUtils.write(new File(FileUtils.getSaveDirectory(gameInfo.name), "game.json"), gameInfo.toJSON().toString(4));
        } catch (IOException e) {
            throw new GameException(GameSaveManager.class, e, "Unable to write game meta info");
        }

        save(game, gameDirectory, filename);
    }

    private void save(Game game, File gameDirectory, String filename) {
        Application.notify(observer -> observer.onCustomEvent("save_game.begin", null));
        long time = System.currentTimeMillis();

        // Create DB file
        File dbFile = new  File(gameDirectory, filename + ".db");
        sqlManager.openDB(dbFile);

        DependencyInjector.getInstance().getSubTypesOf(GameSerializer.class).forEach(gameSerializer -> gameSerializer.save(sqlManager));

        sqlManager.closeDB();
        Log.info("Create onSave game (" + (System.currentTimeMillis() - time) + "ms)");

        sqlManager.post(db -> {
            try {
                // Create zip file
                File archiveFile = new File(gameDirectory, filename + ".zip");
                try (OutputStream archiveStream = new FileOutputStream(archiveFile)) {
                    try (ArchiveOutputStream archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, archiveStream)) {
                        archive.putArchiveEntry(new ZipArchiveEntry(filename + ".db"));
                        try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(dbFile))) {
                            IOUtils.copy(input, archive);
                        }
                        archive.closeArchiveEntry();
                        archive.finish();
                    }
                }

                // Delete DB file
                if (!dbFile.delete()) {
                    throw new IOException("Unable to delete DB file: " + dbFile.getAbsolutePath());
                }

                Log.info("Zip onSave game (" + (System.currentTimeMillis() - time) + "ms)");
                Application.notify(observer -> observer.onCustomEvent("save_game.complete", null));
            } catch (IOException | ArchiveException e) {
                throw new GameException(GameSaveManager.class, e, "Error during game save");
            }
        });
    }
}