package org.smallbox.faraway.core.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.lua.data.DataExtendException;
import org.smallbox.faraway.core.lua.data.LuaExtend;
import org.smallbox.faraway.core.module.ModuleBase;
import org.smallbox.faraway.game.planet.PlanetInfo;
import org.smallbox.faraway.game.planet.RegionInfo;
import org.smallbox.faraway.game.weather.WeatherInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationObject
public class LuaPlanetExtend extends LuaExtend {

    @Override
    public boolean accept(String type) { return "planet".equals(type); }

    @Override
    public void extend(DataManager dataManager, ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String id = getString(value, "id", null);

        PlanetInfo planetInfo = null;
        for (PlanetInfo info: dataManager.planets) {
            if (info.name != null && info.name.equals(id)) {
                planetInfo = info;
            }
        }

        if (planetInfo == null) {
            planetInfo = new PlanetInfo();
            dataManager.planets.add(planetInfo);
        }

        readPlanet(dataManager, planetInfo, value);
    }

    private void readPlanet(DataManager dataManager, PlanetInfo planetInfo, LuaValue value) throws DataExtendException {
        planetInfo.name = getString(value, "id", null);
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
            planetInfo.dayTimes = new ConcurrentHashMap<>();
            for (int i = 1; i <= value.get("day_times").length(); i++) {
                readDayTime(planetInfo.dayTimes, value.get("day_times").get(i));
            }
        }

        if (!value.get("regions").isnil()) {
            planetInfo.regions = new ArrayList<>();
            for (int i = 1; i <= value.get("regions").length(); i++) {
                readRegion(dataManager, planetInfo, value.get("regions").get(i));
            }
        }
    }

    private void readRegion(DataManager dataManager, PlanetInfo planetInfo, LuaValue value) {
        RegionInfo regionInfo = new RegionInfo();

        regionInfo.planet = planetInfo;
        regionInfo.name = getString(value, "id", null);
        regionInfo.label = getString(value, "label", regionInfo.name);
        regionInfo.color = getInt(value, "color", 0x000000ff);
        regionInfo.hostility = getInt(value, "hostility", 2);
        regionInfo.fertility = getInt(value, "fertility", 2);

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

        if (!value.get("months").isnil()) {
            regionInfo.months = new ArrayList<>();
            for (int i = 1; i <= value.get("months").length(); i++) {
                LuaValue luaMonth = value.get("months").get(i);
                RegionInfo.RegionMonth regionMonth = new RegionInfo.RegionMonth();
                regionMonth.index = getInt(luaMonth, "index", 0);
                regionMonth.temperature = new int[] {
                        luaMonth.get("temperature").get(1).toint(),
                        luaMonth.get("temperature").get(2).toint()
                };
                regionMonth.temperatureHourlyVariations = new int[] {
                        luaMonth.get("temperature_hourly_variations").get(1).toint(),
                        luaMonth.get("temperature_hourly_variations").get(2).toint(),
                        luaMonth.get("temperature_hourly_variations").get(3).toint(),
                        luaMonth.get("temperature_hourly_variations").get(4).toint(),
                        luaMonth.get("temperature_hourly_variations").get(5).toint(),
                        luaMonth.get("temperature_hourly_variations").get(6).toint(),
                        luaMonth.get("temperature_hourly_variations").get(7).toint(),
                        luaMonth.get("temperature_hourly_variations").get(8).toint(),
                        luaMonth.get("temperature_hourly_variations").get(9).toint(),
                        luaMonth.get("temperature_hourly_variations").get(10).toint(),
                        luaMonth.get("temperature_hourly_variations").get(11).toint(),
                        luaMonth.get("temperature_hourly_variations").get(12).toint(),
                        luaMonth.get("temperature_hourly_variations").get(13).toint(),
                        luaMonth.get("temperature_hourly_variations").get(14).toint(),
                        luaMonth.get("temperature_hourly_variations").get(15).toint(),
                        luaMonth.get("temperature_hourly_variations").get(16).toint(),
                        luaMonth.get("temperature_hourly_variations").get(17).toint(),
                        luaMonth.get("temperature_hourly_variations").get(18).toint(),
                        luaMonth.get("temperature_hourly_variations").get(19).toint(),
                        luaMonth.get("temperature_hourly_variations").get(20).toint(),
                        luaMonth.get("temperature_hourly_variations").get(21).toint(),
                        luaMonth.get("temperature_hourly_variations").get(22).toint(),
                        luaMonth.get("temperature_hourly_variations").get(23).toint(),
                        luaMonth.get("temperature_hourly_variations").get(24).toint()
                };
                regionMonth.rain = getInt(luaMonth, "rain", 0);
                regionInfo.months.add(regionMonth);
            }
        }

        if (!value.get("seasons").isnil()) {
            regionInfo.seasons = new ArrayList<>();
            for (int i = 1; i <= value.get("seasons").length(); i++) {
                LuaValue luaSeason = value.get("seasons").get(i);
                RegionInfo.RegionSeason regionSeason = new RegionInfo.RegionSeason();
                regionSeason.id = getString(luaSeason, "id", null);
                regionSeason.from = getInt(luaSeason, "from", 0);
                regionSeason.to = getInt(luaSeason, "to", 0);
                regionSeason.dayOfMonth = getInt(luaSeason, "day_of_month", 0);
                regionSeason.name = getString(luaSeason, "name", null);
                regionInfo.seasons.add(regionSeason);
            }
        }

        if (!value.get("weather").isnil()) {
            regionInfo.weather = new ArrayList<>();
            for (int i = 1; i <= value.get("weather").length(); i++) {
                LuaValue luaWeather = value.get("weather").get(i);
                RegionInfo.RegionWeather regionWeatherInfo = new RegionInfo.RegionWeather();
                dataManager.getAsync(luaWeather.get("id").toString(), WeatherInfo.class, weatherInfo -> regionWeatherInfo.info = weatherInfo);
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

    private void readDayTime(Map<Integer, PlanetInfo.DayTime> dayTimes, LuaValue value) {
        dayTimes.put(value.get("hour").toint(), new PlanetInfo.DayTime(
                value.get("hour").toint(),
                value.get("color").optint(0),
                value.get("id").tojstring()
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
