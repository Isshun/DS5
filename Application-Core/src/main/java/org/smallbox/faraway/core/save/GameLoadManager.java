package org.smallbox.faraway.core.save;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.ApplicationException;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.DependencyNotifier;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLoadGame;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnLoadBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnLoadComplete;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameStart;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.log.Log;

import java.io.*;
import java.util.Comparator;

@ApplicationObject
public class GameLoadManager {
    @Inject private DependencyManager dependencyManager;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private GameFileManager gameFileManager;
    @Inject private GameManager gameManager;
    @Inject private SQLManager sqlManager;
    @Inject private DataManager dataManager;
    @Inject private Game game;

    @OnApplicationLoadGame
    public void onLoadGame(GameSaveInfo saveInfo) throws ApplicationException {
        long time = System.currentTimeMillis();
        File gameDirectory = FileUtils.getSaveDirectory(game.getInfo().name);
        String prefixName = saveInfo.filename;

        Log.info("============ LOAD GAME ============");

        dependencyNotifier.notify(OnLoadBegin.class);

        try {
            extractArchive(gameDirectory, prefixName);
            loadDatabaseFile(gameDirectory, prefixName);

            sqlManager.post(db -> {
                System.gc();
                deleteFiles(gameDirectory, prefixName);
                dependencyNotifier.notify(OnLoadComplete.class);
                dependencyNotifier.notify(OnGameStart.class);
                Log.info("Load game completed " + (System.currentTimeMillis() - time));
            });
        } catch (IOException | ArchiveException e) {
            throw new ApplicationException(e, "Cannot load save " + gameDirectory + " / " + prefixName);
        }
    }

    public void loadLastGame() {
        gameFileManager.buildGameList().stream()
                .flatMap(gameInfo -> gameInfo.saveFiles.stream())
                .min((o1, o2) -> o2.date.compareTo(o1.date))
                .ifPresent(saveInfo -> {
                    Log.info("Load save: " + saveInfo);
                    gameManager.loadGame(saveInfo.game, saveInfo);
                });
    }

    private void loadDatabaseFile(File gameDirectory, String prefixName) {
        // Load sqlite DB file
        File dbFile = new File(gameDirectory, prefixName + ".db");
        sqlManager.openDB(dbFile);

        // Call modules serializers
        dependencyManager.getSubTypesOf(GameSerializer.class).stream()
                .sorted(Comparator.comparingInt(value -> value.getPriority().getPriority()))
                .forEach(serializer -> serializer.load(sqlManager));

        sqlManager.closeDB();
    }

    private void deleteFiles(File gameDirectory, String prefixName) {
        File dbFile = new File(gameDirectory, prefixName + ".db");

        if (!dbFile.delete()) {
            Log.warning("Unable to delete " + dbFile.getAbsolutePath());
        }
    }

    private void extractArchive(File gameDirectory, String prefixName) throws IOException, ArchiveException {
        long time = System.currentTimeMillis();

        // Extract ZIP to sqlite DB file
        File archiveFile = new File(gameDirectory, prefixName + ".zip");
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
    }

}