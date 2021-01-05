package org.smallbox.faraway.core.game.save;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.core.module.world.SQLManager;
import org.smallbox.faraway.util.log.Log;

import java.io.*;
import java.util.Comparator;

@ApplicationObject
public class GameLoadManager {
    
    @Inject
    private SQLManager sqlManager;

    @Inject
    private Data data;

    @Inject
    private Game game;

    public void load(File gameDirectory, String prefixName, GameSerializerInterface listener) {
        Log.info("============ LOAD GAME ============");

        Application.notify(observer -> observer.onCustomEvent("load_game.begin", null));
        long time = System.currentTimeMillis();

        try {
            extractArchive(gameDirectory, prefixName);
            loadDatabaseFile(gameDirectory, prefixName);

            sqlManager.post(db -> {
                System.gc();
                listener.onSerializerComplete();
                deleteFiles(gameDirectory, prefixName);
                Application.notify(observer -> observer.onCustomEvent("load_game.complete", null));
                Log.info("Load game completed " + (System.currentTimeMillis() - time));
            });
        } catch (IOException | ArchiveException e) {
            Log.error(e);
        }
    }

    private void loadDatabaseFile(File gameDirectory, String prefixName) {
        // Load sqlite DB file
        File dbFile = new File(gameDirectory, prefixName + ".db");
        sqlManager.openDB(dbFile);

        // Call modules serializers
        DependencyInjector.getInstance().getSubTypesOf(GameSerializer.class).stream()
                .sorted(Comparator.comparingInt(GameSerializer::getModulePriority))
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