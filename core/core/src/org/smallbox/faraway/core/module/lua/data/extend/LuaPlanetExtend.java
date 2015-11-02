package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

import java.util.ArrayList;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaPlanetExtend extends LuaExtend {
    @Override
    public boolean accept(String type) { return "planet".equals(type); }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);

        PlanetInfo planetInfo = null;
        for (PlanetInfo info: GameData.getData().planets) {
            if (info.name != null && info.name.equals(name)) {
                planetInfo = info;
            }
        }

        if (planetInfo == null) {
            planetInfo = new PlanetInfo();
            GameData.getData().planets.add(planetInfo);
        }

        readPlanet(planetInfo, value);

        System.out.println("Extends planet from lua: " + planetInfo.label);
    }

    private void readPlanet(PlanetInfo planetInfo, LuaValue value) throws DataExtendException {
        planetInfo.name = getString(value, "name", null);
        planetInfo.label = getString(value, "label", null);
        planetInfo.cls = getString(value, "class", null);
        planetInfo.desc = getString(value, "description", null);

        if (!value.get("graphics").isnil()) {
            readGraphics(planetInfo, value.get("graphics"));
        }

        if (!value.get("stats").isnil()) {
            readStats(planetInfo, value.get("stats"));
        }

        if (!value.get("hours").isnil()) {
            readHours(planetInfo, value.get("hours"));
        }

        if (!value.get("regions").isnil()) {
            planetInfo.regions = new ArrayList<>();
            for (int i = 1; i <= value.get("regions").length(); i++) {
                readRegion(planetInfo, value.get("regions").get(i));
            }
        }
    }

    private void readRegion(PlanetInfo planetInfo, LuaValue value) {
        RegionInfo regionInfo = new RegionInfo();

        regionInfo.planet = planetInfo;
        regionInfo.name = getString(value, "name", null);
        regionInfo.label = getString(value, "label", regionInfo.name);
        regionInfo.color = getInt(value, "color", 0x000000);

        if (!value.get("temperature").isnil()) {
            regionInfo.temperature = new int[] {
                    value.get("temperature").get(1).toint(),
                    value.get("temperature").get(2).toint()
            };
        }

        if (!value.get("spots").isnil()) {
            regionInfo.spots = new ArrayList<>();
            for (int i = 1; i <= value.get("spots").length(); i++) {
                LuaValue luaSpot = value.get("spots").get(i);
                RegionInfo.RegionDistribution spotInfo = new RegionInfo.RegionDistribution();
                spotInfo.frequency = getDouble(luaSpot, "frequency", 1);
                spotInfo.latitude = new int[] {
                        luaSpot.get("latitude").get(1).toint(),
                        luaSpot.get("latitude").get(2).toint()
                };
                regionInfo.spots.add(spotInfo);
            }
        }

        if (!value.get("terrains").isnil()) {
            regionInfo.terrains = new ArrayList<>();
            for (int i = 1; i <= value.get("terrains").length(); i++) {
                LuaValue luaTerrain = value.get("terrains").get(i);
                RegionInfo.RegionTerrain terrainInfo = new RegionInfo.RegionTerrain();
                terrainInfo.type = getString(luaTerrain, "type", "ground");
                terrainInfo.resource = getString(luaTerrain, "name", null);
                terrainInfo.pattern = getString(luaTerrain, "pattern", null);
                terrainInfo.condition = getString(luaTerrain, "condition", null);
                regionInfo.terrains.add(terrainInfo);
            }
        }

        if (!value.get("weather").isnil()) {
            regionInfo.weather = new ArrayList<>();
            for (int i = 1; i <= value.get("weather").length(); i++) {
                LuaValue luaWeather = value.get("weather").get(i);
                RegionInfo.RegionWeather weatherInfo = new RegionInfo.RegionWeather();
                weatherInfo.name = getString(luaWeather, "name", null);
                weatherInfo.frequency = new double[] {
                        luaWeather.get("frequency").get(1).todouble(),
                        luaWeather.get("frequency").get(2).todouble(),
                };
                weatherInfo.duration = new double[] {
                        luaWeather.get("duration").get(1).todouble(),
                        luaWeather.get("duration").get(2).todouble(),
                };
                regionInfo.weather.add(weatherInfo);
            }
        }

        if (!value.get("fauna").isnil()) {
            regionInfo.fauna = new ArrayList<>();
            for (int i = 1; i <= value.get("fauna").length(); i++) {
                LuaValue luaFauna = value.get("fauna").get(i);
                RegionInfo.RegionFauna faunaInfo = new RegionInfo.RegionFauna();
                faunaInfo.name = getString(luaFauna, "type", "ground");
                faunaInfo.frequency = getDouble(luaFauna, "frequency", 1);
                faunaInfo.number = new int[] {
                        luaFauna.get("count").get(1).toint(),
                        luaFauna.get("count").get(2).toint(),
                };
                regionInfo.fauna.add(faunaInfo);
            }
        }

        planetInfo.regions.add(regionInfo);
    }

    private void readHours(PlanetInfo planetInfo, LuaValue value) {
        planetInfo.hours = new PlanetInfo.PlanetHours();
        planetInfo.hours.dawn = getInt(value, "dawn", 5);
        planetInfo.hours.noon = getInt(value, "noon", 6);
        planetInfo.hours.twilight = getInt(value, "twilight", 19);
        planetInfo.hours.midnight = getInt(value, "midnight", 20);
    }

    private void readStats(PlanetInfo planetInfo, LuaValue value) {
        planetInfo.stats = new PlanetInfo.PlanetStats();
        planetInfo.stats.water = getInt(value, "water", 0);
        planetInfo.stats.fertility = getInt(value, "fertility", 0);
        planetInfo.stats.atmosphere = getInt(value, "atmosphere", 0);
        planetInfo.stats.fauna = getInt(value, "fauna", 0);
        planetInfo.stats.flora = getInt(value, "flora", 0);
        planetInfo.stats.hostile_fauna = getInt(value, "hostile_fauna", 0);
        planetInfo.stats.hostile_humankind = getInt(value, "hostile_humankind", 0);
    }

    private void readGraphics(PlanetInfo planetInfo, LuaValue value) {
    }
}
