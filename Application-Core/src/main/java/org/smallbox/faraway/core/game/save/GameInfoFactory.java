package org.smallbox.faraway.core.game.save;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smallbox.faraway.core.GameScenario;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.util.Constant;

import java.util.UUID;

@ApplicationObject
public class GameInfoFactory {
    @Inject private Data data;

    public JSONObject toJSON(GameInfo gameInfo) {
        JSONObject json = new JSONObject();

        json.put("name", gameInfo.name);
        json.put("planet", gameInfo.planet.name);
        json.put("region", gameInfo.region.name);
        json.put("width", gameInfo.worldWidth);
        json.put("height", gameInfo.worldHeight);
        json.put("floors", gameInfo.worldFloors);

        JSONArray saveArray = new JSONArray();
        for (GameSaveInfo saveInfo: gameInfo.saveFiles) {
            saveArray.put(saveInfo.toJSON());
        }
        json.put("saves", saveArray);

        return json;
    }

    public GameInfo fromJSON(JSONObject json) {
        GameInfo gameInfo = new GameInfo();

        gameInfo.name = json.getString("name");
        gameInfo.planet = data.getPlanet(json.getString("planet"));
        if (gameInfo.planet != null && gameInfo.planet.regions != null) {
            gameInfo.region = gameInfo.planet.regions.stream().filter(region -> region.name.equals(json.getString("region"))).findFirst().orElse(null);
        }
        gameInfo.worldWidth = json.getInt("width");
        gameInfo.worldHeight = json.getInt("height");
        gameInfo.worldFloors = json.getInt("floors");
        gameInfo.groundFloor = json.getInt("floors") - 1;

        if (json.has("saves")) {
            for (int i = 0; i < json.getJSONArray("saves").length(); i++) {
                GameSaveInfo saveInfo = GameSaveInfo.fromJSON(json.getJSONArray("saves").getJSONObject(i));
                saveInfo.game = gameInfo;
                gameInfo.saveFiles.add(saveInfo);
            }
        }

        return gameInfo;
    }

    public GameInfo create(GameScenario scenario) {
        return create(data.getRegion(scenario.planet, scenario.region), scenario.width, scenario.height, scenario.level, scenario.generateMountains);
    }

    public GameInfo create(String planetName, String regionName, int worldWidth, int worldHeight, int worldFloors) {
        return create(data.getRegion(planetName, regionName), worldWidth, worldHeight, worldFloors, true);
    }

    public GameInfo create(RegionInfo regionInfo, int worldWidth, int worldHeight, int worldFloors, boolean generateMountains) {
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
