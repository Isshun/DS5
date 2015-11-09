package org.smallbox.faraway.core.data.serializer;

import com.ximpleware.*;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.AreaModuleSerializer;
import org.smallbox.faraway.core.game.module.character.CharacterModuleSerializer;
import org.smallbox.faraway.core.game.module.world.WorldModuleSerializer;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.FileUtils;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.core.util.OnLoadListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

public class GameSerializer {
    public static class GameSave {
        public int                                      width;
        public int                                      height;
    }

    public static void load(File file, GameSave save) {
        try {
            // open a file and read the content into a byte array
            FileInputStream fis =  new FileInputStream(file);
            byte[] b = new byte[(int)file.length()];
            fis.read(b);
            fis.close();

            VTDGen vg = new VTDGen();
            vg.setDoc(b);
            vg.parse(true);
            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath("/save/width|/save/height");

            new ParamSerializer().load(vn.duplicateNav());
            new WorldModuleSerializer().load(vn.duplicateNav());
            new CharacterModuleSerializer().load(vn.duplicateNav());
            new AreaModuleSerializer().load(vn.duplicateNav());
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

    public static void save(File file) {
        try {
            long time = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(file);

            FileUtils.write(fos, "<?xml version='1.0' encoding='UTF-8'?>");
            FileUtils.write(fos, "<save>");

            // Stock serializer
            (new ParamSerializer()).save(fos);
            (new WorldModuleSerializer()).save(fos);
            (new CharacterModuleSerializer()).save(fos);

            // Modules serializer
            for (GameModule module: ModuleManager.getInstance().getModulesBase()) {
                SerializerInterface serializer = module.getSerializer();
                if (serializer != null) {
                    serializer.save(fos);
                }
            }

            // Modules serializer
            for (GameModule module: ModuleManager.getInstance().getModulesThird()) {
                SerializerInterface serializer = module.getSerializer();
                if (serializer != null) {
                    serializer.save(fos);
                }
            }

            FileUtils.write(fos, "</save>");

            fos.close();
            Log.notice("Save game (" + (System.currentTimeMillis() - time) + "ms)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}