package org.smallbox.faraway;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 08/10/2015.
 */
public class ModuleInfo {
    public static class Required {
        public String id;
        public double minVersion;
    }

    public String           id;
    public String           name;
    public double           version;
    public List<Required>   required;

    public static ModuleInfo fromJSON(JSONObject json) {
        ModuleInfo info = new ModuleInfo();
        info.id = json.getString("id");
        info.name = json.getString("name");
        info.version = json.has("version") ? json.getDouble("version") : 0;
        info.required = new ArrayList<>();
        if (json.has("required")) {
            JSONArray jsonRequired = json.getJSONArray("required");
            for (int i = 0; i < jsonRequired.length(); i++) {
                Required required = new Required();
                required.id = jsonRequired.getJSONObject(i).getString("module");
                required.minVersion = jsonRequired.getJSONObject(i).getDouble("min_version");
                info.required.add(required);
            }
        }
        return info;
    }
}
