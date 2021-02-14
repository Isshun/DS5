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
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnLoadBegin;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnLoadComplete;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameSerializer;
import org.smallbox.faraway.util.log.Log;

import java.io.*;
import java.util.Comparator;

@ApplicationObject
public class GameLoadManager {
    @Inject private DependencyManager dependencyManager;
    @Inject private DependencyNotifier dependencyNotifier;
    @Inject private SQLManager sqlManager;
    @Inject private DataManager dataManager;
    @Inject private Game game;

    public void load(File gameDirectory, String prefixName, GameSerializerInterface listener) throws ApplicationException {
        Log.info("============ LOAD GAME ============");

        dependencyNotifier.notify(OnLoadBegin.class);
        long time = System.currentTimeMillis();

        try {
            extractArchive(gameDirectory, prefixName);
            loadDatabaseFile(gameDirectory, prefixName);

            sqlManager.post(db -> {
                System.gc();
                listener.onSerializerComplete();
                deleteFiles(gameDirectory, prefixName);
                dependencyNotifier.notify(OnLoadComplete.class);
                Log.info("Load game completed " + (System.currentTimeMillis() - time));
            });
        } catch (IOException | ArchiveException e) {
            throw new ApplicationException(e, "Cannot load save " + gameDirectory + " / " + prefixName);
        }
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