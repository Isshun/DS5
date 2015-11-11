package org.smallbox.faraway.core.game;

import com.almworks.sqlite4java.SQLiteConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.util.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alex on 30/08/2015.
 */
public class GameInfo {
    public enum Type {INIT, AUTO, FAST, REGULAR}

    public static class GameSaveInfo {
        public Type             type;
        public String           filename;
        public String           label;
        public Date             date;
    }

    public PlanetInfo           planet;
    public RegionInfo           region;
    public String               name;
    public List<GameSaveInfo>   saveFiles = new ArrayList<>();
    public int                  worldWidth;
    public int                  worldHeight;
    public int                  worldFloors;

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        json.put("name", name);
        json.put("planet", planet.name);
        json.put("region", region.name);
        json.put("width", worldWidth);
        json.put("height", worldHeight);
        json.put("floors", worldFloors);

        JSONArray saveArray = new JSONArray();
        for (GameSaveInfo saveInfo: saveFiles) {
            JSONObject saveJson = new JSONObject();
            saveJson.put("type", saveInfo.type.toString());
            saveJson.put("filename", saveInfo.filename);
            saveJson.put("date", new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(saveInfo.date));
            saveArray.put(saveJson);
        }
        json.put("saves", saveArray);

        return json;
    }

    public static GameInfo fromJSON(JSONObject json) {
        GameInfo info = new GameInfo();

        info.name = json.getString("name");
        info.planet = Data.getData().getPlanet(json.getString("planet"));
        info.region = info.planet.regions.stream().filter(region -> region.name.equals(json.getString("region"))).findFirst().get();
        info.worldWidth = json.getInt("width");
        info.worldHeight = json.getInt("height");
        info.worldFloors = json.getInt("floors");

        if (json.has("saves")) {
            for (int i = 0; i < json.getJSONArray("saves").length(); i++) {
                JSONObject jsonSave = json.getJSONArray("saves").getJSONObject(i);
                GameSaveInfo saveInfo = new GameSaveInfo();
                saveInfo.type = Type.valueOf(jsonSave.getString("type").toUpperCase());
                saveInfo.filename = jsonSave.getString("filename");
                try {
                    saveInfo.date = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").parse(jsonSave.getString("date"));
                    saveInfo.label = new SimpleDateFormat("dd/MM/YYYY - hh:mm:ss").format(saveInfo.date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                info.saveFiles.add(saveInfo);
            }
        }

        return info;
    }

    public static GameInfo create(RegionInfo regionInfo, int worldWidth, int worldHeight, int worldFloors) {
        assert worldWidth <= Constant.MAX_WORLD_WIDTH;
        assert worldHeight <= Constant.MAX_WORLD_HEIGHT;
        assert worldFloors <= Constant.MAX_WORLD_FLOORS;

        GameInfo info = new GameInfo();

        info.worldWidth = worldWidth;
        info.worldHeight = worldHeight;
        info.worldFloors = worldFloors;
        info.planet = regionInfo.planet;
        info.region = regionInfo;
        info.name = UUID.randomUUID().toString();

        return info;
    }
}
