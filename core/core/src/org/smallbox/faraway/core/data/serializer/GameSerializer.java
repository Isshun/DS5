package org.smallbox.faraway.core.data.serializer;

import com.almworks.sqlite4java.SQLiteConnection;
import com.ximpleware.*;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.module.world.DBRunnable;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.WorldModuleSerializer;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;

import java.io.*;

public class GameSerializer {
    public static class GameSave {
        public int                                      width;
        public int                                      height;
    }

    public interface GameSerializerInterface {
        void onSerializerComplete();
    }

    public static void load(GameInfo gameInfo, File gameDirectory, String filename, GameSerializerInterface listener) {
        Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.begin", null));
        SQLHelper.getInstance().openDB(new File(gameDirectory, filename + ".db"));
        ModuleManager.getInstance().getSerializers().forEach(serializer -> serializer.load(gameInfo));
        SQLHelper.getInstance().closeDB();
        SQLHelper.getInstance().post(db -> {
            listener.onSerializerComplete();
            Application.getInstance().notify(observer -> observer.onCustomEvent("load_game.complete", null));
        });
    }

    public static void save(File gameDirectory, String filename) {
        long time = System.currentTimeMillis();

        SQLHelper.getInstance().openDB(new File(gameDirectory, filename + ".db"));
        ModuleManager.getInstance().getSerializers().forEach(SerializerInterface::save);
        SQLHelper.getInstance().closeDB();

        SQLHelper.getInstance().post(db -> {
            File source = new  File(gameDirectory, filename + ".db");
            File destination = new File(gameDirectory, filename + ".zip");

            Log.notice("Save game (" + (System.currentTimeMillis() - time) + "ms)");

            try {
                OutputStream archiveStream = new FileOutputStream(destination);
                ArchiveOutputStream archive = new ArchiveStreamFactory().createArchiveOutputStream(ArchiveStreamFactory.ZIP, archiveStream);

                String entryName = filename + ".db";
                ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
                archive.putArchiveEntry(entry);

                BufferedInputStream input = new BufferedInputStream(new FileInputStream(source));

                IOUtils.copy(input, archive);
                input.close();
                archive.closeArchiveEntry();

                archive.finish();
                archiveStream.close();
            } catch (IOException | ArchiveException e) {
                e.printStackTrace();
            }

            Log.notice("Save game (" + (System.currentTimeMillis() - time) + "ms)");
        });
    }
}