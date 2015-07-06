package org.smallbox.faraway.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.util.FileUtils;
import org.smallbox.faraway.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GameSerializer {
    public static class GameSave {
        public int                                      width;
        public int                                      height;
    }

    public static GameSave preLoad(String filePath, LoadListener loadListener) {
        long time = System.currentTimeMillis();
        System.gc();

        if (loadListener != null) {
            loadListener.onUpdate("Load savegame [" + filePath + "]");
        }
        Log.info("Load savegame [" + filePath + "]");

        return null;
    }

    public static void load(String filePath, GameSave save) {
        try {
            // open a file and read the content into a byte array
            File f = new File(filePath);
            FileInputStream fis =  new FileInputStream(f);
            byte[] b = new byte[(int)f.length()];
            fis.read(b);
            fis.close();

            VTDGen vg = new VTDGen();
            vg.setDoc(b);
            vg.parse(true);
            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath("/save/width|/save/height");

            new WorldSerializer().load(vn.duplicateNav());
            new CharacterSerializer().load(vn.duplicateNav());
            new AreaSerializer().load(vn.duplicateNav());
        } catch (ParseException e){
            System.out.println(" XML file parsing error \n" + e);
        } catch (NavException e){
            System.out.println(" Exception during navigation " + e);
        } catch (java.io.IOException e){
            System.out.println(" IO exception condition" + e);
        } catch (XPathEvalException | XPathParseException e) {
            e.printStackTrace();
        }
    }

    public static void save(String filePath) {
        try {
            long time = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(new File(filePath));

            FileUtils.write(fos, "<?xml version='1.0' encoding='UTF-8'?>");
            FileUtils.write(fos, "<save>");

            (new WorldSerializer()).save(fos);
            (new CharacterSerializer()).save(fos);
            (new AreaSerializer()).save(fos);

            FileUtils.write(fos, "</save>");

            fos.close();
            Log.notice("Save game (" + (System.currentTimeMillis() - time) + "ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
