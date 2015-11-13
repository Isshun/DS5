package org.smallbox.faraway.core.data.serializer;

import com.almworks.sqlite4java.SQLiteConnection;
import com.ximpleware.*;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.module.world.DBRunnable;
import org.smallbox.faraway.core.game.module.world.SQLHelper;
import org.smallbox.faraway.core.game.module.world.WorldModuleSerializer;
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
        SQLHelper.getInstance().openDB(new File(gameDirectory, filename + ".db"));

        try {
            new WorldModuleSerializer().load(gameInfo, null, () -> {
                listener.onSerializerComplete();
            });
        } catch (XPathParseException | XPathEvalException | NavException e) {
            e.printStackTrace();
        }

        SQLHelper.getInstance().closeDB();

//        try {
//            // open a file and read the content into a byte array
//            FileInputStream fis =  new FileInputStream(file);
//            byte[] b = new byte[(int)file.length()];
//            fis.read(b);
//            fis.close();
//
//            VTDGen vg = new VTDGen();
//            vg.setDoc(b);
//            vg.parse(true);
//            VTDNav vn = vg.getNav();
//            AutoPilot ap = new AutoPilot(vn);
//            ap.selectXPath("/save/width|/save/height");
//
//            new ParamSerializer().load(vn.duplicateNav());
//            new WorldModuleSerializer().load(vn.duplicateNav());
//            new CharacterModuleSerializer().load(vn.duplicateNav());
//            new AreaModuleSerializer().load(vn.duplicateNav());
//        } catch (ParseException e){
//            System.out.println(" XML file parsing error \n" + e);
//        } catch (NavException e){
//            System.out.println(" Exception during navigation " + e);
//        } catch (java.io.IOException e){
//            System.out.println(" IO exception condition" + e);
//        } catch (XPathEvalException | XPathParseException e) {
//            e.printStackTrace();
//        }
    }

    public static void save(File gameDirectory, String filename) {
        long time = System.currentTimeMillis();

        SQLHelper.getInstance().openDB(new File(gameDirectory, filename + ".db"));

//            FileOutputStream fos = new FileOutputStream(file);
//
//            FileUtils.write(fos, "<?xml version='1.0' encoding='UTF-8'?>");
//            FileUtils.write(fos, "<save>");
//
        // Stock serializer
//            (new ParamSerializer()).save(fos);
        (new WorldModuleSerializer()).save(null);
//            (new CharacterModuleSerializer()).save(fos);
//
//            // Modules serializer
//            for (GameModule module: ModuleManager.getInstance().getModulesBase()) {
//                SerializerInterface serializer = module.getSerializer();
//                if (serializer != null) {
//                    serializer.save(fos);
//                }
//            }
//
//            // Modules serializer
//            for (GameModule module: ModuleManager.getInstance().getModulesThird()) {
//                SerializerInterface serializer = module.getSerializer();
//                if (serializer != null) {
//                    serializer.save(fos);
//                }
//            }
//
//            FileUtils.write(fos, "</save>");
//
//            fos.close();
        SQLHelper.getInstance().closeDB();

        SQLHelper.getInstance().post(new DBRunnable() {
            @Override
            public void run(SQLiteConnection db) {
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
            }
        });
    }
}