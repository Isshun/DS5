package org.smallbox.faraway.data.loader;

import org.smallbox.faraway.game.model.GameData;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Alex on 18/06/2015.
 */
public class StringLoader implements IDataLoader {
    private long    _lastConfigModified;

    @Override
    public void load(GameData data) {
        try {
            data.strings = new HashMap<>();
            File file = new File("data/strings/" + data.config.lang + ".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(":") && !line.startsWith("#")) {
                    int sep = line.indexOf(':');
                    if (line.contains("\"")) {
                        sep = line.indexOf(':', line.indexOf('"', line.indexOf('"')+1)+1);
                    }

                    String key = line.substring(0, sep).trim().replace("\"", "");
                    String value  = line.substring(sep + 1).trim().replace("\"", "");
                    data.strings.put(key, value);
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadIfNeeded(GameData data) {
        long lastConfigModified = new File("data/config.yml").lastModified();
        if (lastConfigModified > _lastConfigModified) {
            load(data);
            data.needUIRefresh = true;
            _lastConfigModified = lastConfigModified;
        }
    }
}
