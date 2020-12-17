package org.smallbox.faraway.core.engine.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.engine.module.ModuleBase;
import org.smallbox.faraway.core.engine.module.lua.data.DataExtendException;
import org.smallbox.faraway.core.engine.module.lua.data.LuaExtend;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.model.planet.RegionInfo;
import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaPlanetExtend extends LuaExtend {

    @Override
    public boolean accept(String type) { return "planet".equals(type); }

    @Override
    public void extend(Data data, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);

        PlanetInfo planetInfo = null;
        for (PlanetInfo info: data.planets) {
            if (info.name != null && info.name.equals(name)) {
                planetInfo = info;
            }
        }

        if (planetInfo == null) {
            planetInfo = new PlanetInfo();
            data.planets.add(planetInfo);
        }

        readPlanet(data, planetInfo, value);
    }

    private void readPlanet(Data data, PlanetInfo planetInfo, LuaValue value) throws DataExtendException {
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

        if (!value.get("day_times").isnil()) {
            planetInfo.dayTimes = new ArrayList<>();
            for (int i = 1; i <= value.get("day_times").length(); i++) {
                readDayTime(planetInfo.dayTimes, value.get("day_times").get(i));
            }
        }

        if (!value.get("regions").isnil()) {
            planetInfo.regions = new ArrayList<>();
            for (int i = 1; i <= value.get("regions").length(); i++) {
                readRegion(data, planetInfo, value.get("regions").get(i));
            }
        }
    }

    private void readRegion(Data data, PlanetInfo planetInfo, LuaValue value) {
        RegionInfo regionInfo = new RegionInfo();

        regionInfo.planet = planetInfo;
        regionInfo.name = getString(value, "name", null);
        regionInfo.label = getString(value, "label", regionInfo.name);
        regionInfo.color = getInt(value, "color", 0x000000ff);

        if (!value.get("temperatures").isnil()) {
            regionInfo.temperatures = new ArrayList<>();
            for (int i = 1; i <= value.get("temperatures").length(); i++) {
                LuaValue luaTemperature = value.get("temperatures").get(i);
                RegionInfo.RegionTemperature regionTemperature = new RegionInfo.RegionTemperature();
                regionTemperature.temperature = getDoubleInterval(luaTemperature, "value", null);
                regionTemperature.fromFloor = Math.min(luaTemperature.get("floors").get(1).toint(), luaTemperature.get("floors").get(2).toint());
                regionTemperature.toFloor = Math.max(luaTemperature.get("floors").get(1).toint(), luaTemperature.get("floors").get(2).toint());
                regionInfo.temperatures.add(regionTemperature);
            }
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
                terrainInfo.ground = getString(luaTerrain, "ground", null);
                terrainInfo.liquid = getString(luaTerrain, "liquid", null);
                terrainInfo.resource = getString(luaTerrain, "resource", null);
                terrainInfo.pattern = getString(luaTerrain, "pattern", null);
                terrainInfo.condition = getString(luaTerrain, "condition", null);
                regionInfo.terrains.add(terrainInfo);
            }
        }

        if (!value.get("weather").isnil()) {
            regionInfo.weather = new ArrayList<>();
            for (int i = 1; i <= value.get("weather").length(); i++) {
                LuaValue luaWeather = value.get("weather").get(i);
                RegionInfo.RegionWeather regionWeatherInfo = new RegionInfo.RegionWeather();
                data.getAsync(luaWeather.get("name").toString(), WeatherInfo.class, weatherInfo -> regionWeatherInfo.info = weatherInfo);
                regionWeatherInfo.frequency = new double[] {
                        luaWeather.get("frequency").get(1).todouble(),
                        luaWeather.get("frequency").get(2).todouble(),
                };
                regionWeatherInfo.duration = new double[] {
                        luaWeather.get("duration").get(1).todouble(),
                        luaWeather.get("duration").get(2).todouble(),
                };
                regionInfo.weather.add(regionWeatherInfo);
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

    private void readDayTime(List<PlanetInfo.DayTime> dayTimes, LuaValue value) {
        dayTimes.add(new PlanetInfo.DayTime(
                value.get("hour").toint(),
                value.get("color").optlong(0),
                value.get("name").tojstring()
        ));
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
        if (!value.get("thumb").isnil()) { planetInfo.graphics.thumb = readGraphic(value.get("thumb")); }
        if (!value.get("background").isnil()) { planetInfo.graphics.background = readGraphic(value.get("background")); }
    }

    private PlanetInfo.PlanetGraphicInfo readGraphic(LuaValue value) {
        PlanetInfo.PlanetGraphicInfo planetGraphicInfo = new PlanetInfo.PlanetGraphicInfo();
        planetGraphicInfo.path = getString(value, "path", null);
        return planetGraphicInfo;
    }
}