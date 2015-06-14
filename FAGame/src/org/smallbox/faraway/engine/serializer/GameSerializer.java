package org.smallbox.faraway.engine.serializer;

import com.thoughtworks.xstream.XStream;
import org.smallbox.faraway.engine.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GameSerializer {
    public static class GameSave {
        public List<WorldSerializer.WorldSaveParcel>    parcels;
        public List<CharacterSerializer.CharacterSave>	characters;
        public List<JobSerializer.JobSave> 				jobs;
        public List<AreaSerializer.AreaSave> 		    areas;

        public GameSave() {
            areas = new ArrayList<>();
        }
    }

    public static void load(String filePath, LoadListener loadListener) {
        System.gc();

        loadListener.onUpdate("Load savegame [" + filePath + "]");
        Log.info("Load savegame [" + filePath + "]");
        long time = System.currentTimeMillis();

        // Open XML
        try {
            InputStream input = new FileInputStream(filePath + ".xml");
            XStream xstream = new XStream();
            load((GameSave)xstream.fromXML(input), loadListener);
            input.close();
            System.gc();
            Log.info("Game loaded: " + (System.currentTimeMillis() - time) + "ms");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(GameSave save, LoadListener loadListener) {
        if (save != null) {
            if (save != null) {
                if (save.characters != null) {
                    loadListener.onUpdate("Loading characters");
                    (new CharacterSerializer()).load(save);
                }

                if (save.parcels != null) {
                    loadListener.onUpdate("Loading world");
                    (new WorldSerializer()).load(save);
                }

                if (save.jobs != null) {
                    loadListener.onUpdate("Loading jobs");
                    (new JobSerializer()).load(save);
                }

                if (save.areas != null) {
                    loadListener.onUpdate("Loading areas");
                    (new AreaSerializer()).load(save);
                }
            }
        }
    }

    public static void save(String filePath) {
        System.gc();

        // Construct save object
        GameSave save = new GameSave();
        (new CharacterSerializer()).save(save);
        (new JobSerializer()).save(save);
        (new AreaSerializer()).save(save);
        (new WorldSerializer()).save(save);

        // Write XML
        XStream xstream = new XStream();
        String xml = xstream.toXML(save);

        try {
            FileOutputStream fs = new FileOutputStream(filePath + ".xml");
            OutputStreamWriter output = new OutputStreamWriter(fs);
            output.write(xml);
            output.close();
            fs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
