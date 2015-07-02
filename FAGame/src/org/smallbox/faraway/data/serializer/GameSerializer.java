package org.smallbox.faraway.data.serializer;

import com.thoughtworks.xstream.XStream;
import com.ximpleware.*;
import org.smallbox.faraway.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameSerializer {
    public static class GameSave {
//        public List<WorldSerializer.WorldSaveParcel>    parcels;
//        public List<CharacterSerializer.CharacterSave>	characters;
        public List<JobSerializer.JobSave> 				jobs;
        public List<AreaSerializer.AreaSave> 		    areas;
        public int                                      width;
        public int                                      height;
//        public List<ParcelTypeSave>                     types;

        public GameSave() {
            areas = new ArrayList<>();
        }
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
//
//    public static void load(String filePath, LoadListener loadListener) {
//        System.gc();
//
//        loadListener.onUpdate("Load savegame [" + filePath + "]");
//        Log.info("Load savegame [" + filePath + "]");
//        long time = System.currentTimeMillis();
//
//        // Open XML
//        try {
//            InputStream input = new FileInputStream(filePath);
//            XStream xstream = new XStream();
//            load((GameSave)xstream.fromXML(input), loadListener);
//            input.close();
//            System.gc();
//            Log.info("Game loaded: " + (System.currentTimeMillis() - time) + "ms");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void load(String filePath, GameSave save) {
//        // Open XML
//        try {
//            long time = System.currentTimeMillis();
//            InputStream input = new FileInputStream(filePath);
//            save = (GameSave)new XStream().fromXML(input);
//            input.close();
//            System.gc();
//            Log.info("Game loaded: " + (System.currentTimeMillis() - time) + "ms");
//
////            new WorldSerializer().load(save);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Log.error("Unable to load saved game: " + filePath);
//        Log.info("Game loaded: " + (System.currentTimeMillis() - time) + "ms");


        try {
            // open a file and read the content into a byte array
            File f = new File(filePath);
            FileInputStream fis =  new FileInputStream(f);
            byte[] b = new byte[(int) f.length()];
            fis.read(b);
            //
            VTDGen vg = new VTDGen();
            vg.setDoc(b);
            vg.parse(true);  // set namespace awareness to true
            VTDNav vn = vg.getNav();
            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath("/save/width|/save/height");

            new NewWorldSerializer().load(vn.duplicateNav());
            new NewCharacterSerializer().load(vn.duplicateNav());
        }
        catch (ParseException e){
            System.out.println(" XML file parsing error \n"+e);
        }
        catch (NavException e){
            System.out.println(" Exception during navigation "+e);
        }
        catch (java.io.IOException e){
            System.out.println(" IO exception condition"+e);
        } catch (XPathEvalException e) {
            e.printStackTrace();
        } catch (XPathParseException e) {
            e.printStackTrace();
        }

//
//
//        if (save != null) {
//            if (save != null) {
//                if (save.characters != null) {
////                    loadListener.onUpdate("Loading list");
//                    (new NewCharacterSerializer()).load(save);
//                }
//
////                if (save.parcels != null) {
//////                    loadListener.onUpdate("Loading world");
////                    (new NewWorldSerializer()).load(save);
////                }
//
//                if (save.jobs != null) {
////                    loadListener.onUpdate("Loading jobs");
//                    (new JobSerializer()).load(save);
//                }
//
//                if (save.areas != null) {
////                    loadListener.onUpdate("Loading areas");
//                    (new AreaSerializer()).load(save);
//                }
//            }
//        }
    }

    public static void save(String filePath) {
        (new NewWorldSerializer()).save(null);

//        Log.info("Save game: " + filePath);
//        System.gc();
//
//        // Construct save object
//        GameSave save = new GameSave();
//        (new NewCharacterSerializer()).save(save);
//        (new JobSerializer()).save(save);
//        (new AreaSerializer()).save(save);
//        (new NewWorldSerializer()).save(save);
//
//        // Write XML
//        XStream xstream = new XStream();
//        String xml = xstream.toXML(save);
//
//        try {
//            FileOutputStream fs = new FileOutputStream(filePath);
//            OutputStreamWriter output = new OutputStreamWriter(fs);
//            output.write(xml);
//            output.close();
//            fs.close();
//
//            System.gc();
//            Log.info("Save game: " + filePath + " done");
//
//            return;
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Log.error("Save game: " + filePath + " failed");
    }

}
