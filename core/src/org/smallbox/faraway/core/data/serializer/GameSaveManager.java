package org.smallbox.faraway.core.data.serializer;

import org.apache.commons.compress.archivers.*;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.util.Log;

import java.io.*;

public class GameSaveManager {
    public interface GameSerializerInterface {
        void onSerializerComplete();
    }

    public static void load(Game game, File gameDirectory, String filename, GameSerializerInterface listener) {
        Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.begin", null));
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
            SQLHelper.getInstance().openDB(dbFile);
            new GameSerializer().load(game);
            ModuleManager.getInstance().getSerializers().forEach(serializer -> serializer.load(game));
            SQLHelper.getInstance().closeDB();

            SQLHelper.getInstance().post(db -> {
                listener.onSerializerComplete();
                dbFile.delete();
                Log.info("Load save game: " + (System.currentTimeMillis() - time));
                Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.complete", null));
            });
        } catch (IOException | ArchiveException e) {
            e.printStackTrace();
        }
    }

    public static void save(File gameDirectory, String filename) {
        Application.getInstance().notify(observer -> observer.onCustomEvent("save_game.begin", null));
        long time = System.currentTimeMillis();

        // Create DB file
        File dbFile = new  File(gameDirectory, filename + ".db");
        SQLHelper.getInstance().openDB(dbFile);
        ModuleManager.getInstance().getSerializers().forEach(SerializerInterface::save);
        SQLHelper.getInstance().closeDB();
        Log.notice("Create save game (" + (System.currentTimeMillis() - time) + "ms)");

        SQLHelper.getInstance().post(db -> {
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

                Log.notice("Zip save game (" + (System.currentTimeMillis() - time) + "ms)");
                Application.getInstance().notify(observer -> observer.onCustomEvent("save_game.complete", null));
            } catch (IOException | ArchiveException e) {
                e.printStackTrace();
            }
        });
    }
}