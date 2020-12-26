package org.smallbox.faraway.core.game;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smallbox.faraway.core.GameException;
import org.smallbox.faraway.core.GameScenario;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.util.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class GameInfo {
    public boolean generateMountains;

    public enum Type {INIT, AUTO, FAST, REGULAR}

    public static class GameSaveInfo {
        public GameInfo         game;
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
    public int                  groundFloor;

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
            saveJson.put("date", new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).format(saveInfo.date));
            saveArray.put(saveJson);
        }
        json.put("saves", saveArray);

        return json;
    }

    public static GameInfo fromJSON(JSONObject json) {
        GameInfo gameInfo = new GameInfo();

        gameInfo.name = json.getString("name");
        gameInfo.planet = DependencyInjector.getInstance().getDependency(Data.class).getPlanet(json.getString("planet"));
        if (gameInfo.planet != null && gameInfo.planet.regions != null) {
            gameInfo.region = gameInfo.planet.regions.stream().filter(region -> region.name.equals(json.getString("region"))).findFirst().get();
        }
        gameInfo.worldWidth = json.getInt("width");
        gameInfo.worldHeight = json.getInt("height");
        gameInfo.worldFloors = json.getInt("floors");
        gameInfo.groundFloor = json.getInt("floors") - 1;

        if (json.has("saves")) {
            for (int i = 0; i < json.getJSONArray("saves").length(); i++) {
                JSONObject jsonSave = json.getJSONArray("saves").getJSONObject(i);
                GameSaveInfo saveInfo = new GameSaveInfo();
                saveInfo.game = gameInfo;
                saveInfo.type = Type.valueOf(jsonSave.getString("type").toUpperCase());
                saveInfo.filename = jsonSave.getString("filename");
                try {
                    saveInfo.date = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.ENGLISH).parse(jsonSave.getString("date"));
                    saveInfo.label = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss", Locale.ENGLISH).format(saveInfo.date);
                } catch (ParseException e) {
                    throw new GameException(GameInfo.class, "Cannot read GameInfo json");
                }
                gameInfo.saveFiles.add(saveInfo);
            }
        }

        return gameInfo;
    }

    public static GameInfo create(GameScenario scenario) {
        return create(DependencyInjector.getInstance().getDependency(Data.class).getRegion(scenario.planet, scenario.region), scenario.width, scenario.height, scenario.level, scenario.generateMountains);
    }

    public static GameInfo create(String planetName, String regionName, int worldWidth, int worldHeight, int worldFloors) {
        return create(DependencyInjector.getInstance().getDependency(Data.class).getRegion(planetName, regionName), worldWidth, worldHeight, worldFloors, true);
    }

    public static GameInfo create(RegionInfo regionInfo, int worldWidth, int worldHeight, int worldFloors, boolean generateMountains) {
        assert worldWidth <= Constant.MAX_WORLD_WIDTH;
        assert worldHeight <= Constant.MAX_WORLD_HEIGHT;
        assert worldFloors <= Constant.MAX_WORLD_FLOORS;

        GameInfo info = new GameInfo();

        info.worldWidth = worldWidth;
        info.worldHeight = worldHeight;
        info.worldFloors = worldFloors;
        info.groundFloor = worldFloors - 1;
        info.planet = regionInfo.planet;
        info.region = regionInfo;
        info.name = UUID.randomUUID().toString();
        info.generateMountains = generateMountains;

        return info;
    }
}
