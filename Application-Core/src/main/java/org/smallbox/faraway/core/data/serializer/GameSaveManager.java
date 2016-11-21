package org.smallbox.faraway.core.data.serializer;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.ModuleSerializer;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.util.Log;

import java.io.*;

public class GameSaveManager {
    public interface GameSerializerInterface {
        void onSerializerComplete();
    }

    public static void load(Game game, File gameDirectory, String filename, GameSerializerInterface listener) {
        Application.notify(observer -> observer.onCustomEvent("load_game.begin", null));
        long time = System.currentTimeMillis();

        try {
            File archiveFile = new File(gameDirectory, filename + ".zip");

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
            File dbFile = new File(gameDirectory, filename + ".db");
            Application.sqlManager.openDB(dbFile);

            game.getModules().stream()
                    .filter(module -> module.getClass().isAnnotationPresent(ModuleSerializer.class))
                    .forEach(module -> {
                        GameSerializer<GameModule> serializer = GameSerializer.createSerializer(module);
                        if (serializer != null) {
                            serializer.load(module, game);
                        } else {
                            throw new RuntimeException("Unable to find serializer for module: " + module);
                        }
                    });

            Application.sqlManager.closeDB();

            Application.sqlManager.post(db -> {
                listener.onSerializerComplete();
                // TODO
//                dbFile.delete();
                Log.info("Load onSave game: " + (System.currentTimeMillis() - time));
                Application.notify(observer -> observer.onCustomEvent("load_game.complete", null));
            });
        } catch (IOException | ArchiveException e) {
            e.printStackTrace();
        }
    }

    public static void save(Game game, File gameDirectory, String filename) {
        Application.notify(observer -> observer.onCustomEvent("save_game.begin", null));
        long time = System.currentTimeMillis();

        // Create DB file
        File dbFile = new  File(gameDirectory, filename + ".db");
        Application.sqlManager.openDB(dbFile);

        game.getModules().stream()
                .filter(module -> module.getClass().isAnnotationPresent(ModuleSerializer.class))
                .forEach(module -> {
                    GameSerializer<GameModule> serializer = GameSerializer.createSerializer(module);
                    if (serializer != null) {
                        serializer.save(module, game);
                    } else {
                        throw new RuntimeException("Unable to find serializer");
                    }
                });

        Application.sqlManager.closeDB();
        Log.notice("Create onSave game (" + (System.currentTimeMillis() - time) + "ms)");

        Application.sqlManager.post(db -> {
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
                dbFile.delete();

                Log.notice("Zip onSave game (" + (System.currentTimeMillis() - time) + "ms)");
                Application.notify(observer -> observer.onCustomEvent("save_game.complete", null));
            } catch (IOException | ArchiveException e) {
                e.printStackTrace();
            }
        });
    }
}