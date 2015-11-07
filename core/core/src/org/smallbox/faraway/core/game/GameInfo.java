package org.smallbox.faraway.core.game;

import org.json.JSONObject;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public class GameInfo {
    public enum Type {AUTO, FAST, REGULAR}

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
    public int                  worldWidth = 50;
    public int                  worldHeight = 50;

    public static GameInfo fromJSON(JSONObject json) {
        GameInfo info = new GameInfo();

        info.name = json.getString("name");
        info.planet = Data.getData().getPlanet(json.getString("planet"));
        info.region = info.planet.regions.stream().filter(region -> region.name.equals(json.getString("region"))).findFirst().get();

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

    public static GameInfo create(RegionInfo regionInfo) {
        GameInfo info = new GameInfo();

        info.planet = regionInfo.planet;
        info.region = regionInfo;

        return info;
    }
}
