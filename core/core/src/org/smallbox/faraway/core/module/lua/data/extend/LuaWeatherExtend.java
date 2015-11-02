package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.model.WeatherModel;
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
public class LuaWeatherExtend extends LuaExtend {
    @Override
    public boolean accept(String type) { return "weather".equals(type); }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);

        WeatherModel weatherModel = GameData.getData().weathers.get(name);
        if (weatherModel == null) {
            weatherModel = new WeatherModel();
            GameData.getData().weathers.put(name, weatherModel);
        }

        readWeather(weatherModel, value);

        System.out.println("Extends weather from lua: " + weatherModel.label);
    }

    private void readWeather(WeatherModel weatherModel, LuaValue value) throws DataExtendException {
        weatherModel.name = getString(value, "name", null);
        weatherModel.label = getString(value, "label", null);
        weatherModel.icon = getString(value, "icon", null);
        weatherModel.particle = getString(value, "particle", null);
        weatherModel.duration = getIntInterval(value, "duration", null);
        weatherModel.temperatureChange = getIntInterval(value, "temperatureChange", null);

        if (!value.get("conditions").isnil()) {
            weatherModel.condition = new WeatherModel.WeatherCondition();
            if (!value.get("conditions").get("temperature").isnil()) {
                weatherModel.condition.temperature = getIntInterval(value.get("conditions"), "temperature", null);
            }
        }

        if (!value.get("sun").isnil()) {
            weatherModel.sun = new WeatherModel.WeatherSunModel();
            weatherModel.sun.dawn = getInt(value.get("sun"), "dawn", 5);
            weatherModel.sun.noon = getInt(value.get("sun"), "noon", 6);
            weatherModel.sun.twilight = getInt(value.get("sun"), "twilight", 18);
            weatherModel.sun.midnight = getInt(value.get("sun"), "midnight", 19);
        }
    }
}
