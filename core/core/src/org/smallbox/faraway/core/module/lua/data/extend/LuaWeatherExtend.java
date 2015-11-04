package org.smallbox.faraway.core.module.lua.data.extend;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.model.WeatherInfo;
import org.smallbox.faraway.core.module.lua.DataExtendException;
import org.smallbox.faraway.core.module.lua.LuaModule;
import org.smallbox.faraway.core.module.lua.LuaModuleManager;
import org.smallbox.faraway.core.module.lua.data.LuaExtend;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaWeatherExtend extends LuaExtend {
    @Override
    public boolean accept(String type) { return "weather".equals(type); }

    @Override
    public void extend(LuaModuleManager luaModuleManager, LuaModule module, Globals globals, LuaValue value) throws DataExtendException {
        String name = getString(value, "name", null);

        WeatherInfo weatherInfo = Data.getData().weathers.get(name);
        if (weatherInfo == null) {
            weatherInfo = new WeatherInfo();
            Data.getData().weathers.put(name, weatherInfo);
        }

        readWeather(weatherInfo, value);

        System.out.println("Extends weather from lua: " + weatherInfo.label);
    }

    private void readWeather(WeatherInfo weatherInfo, LuaValue value) throws DataExtendException {
        weatherInfo.name = getString(value, "name", null);
        weatherInfo.label = getString(value, "label", null);
        weatherInfo.icon = getString(value, "icon", null);
        weatherInfo.particle = getString(value, "particle", null);
        weatherInfo.duration = getIntInterval(value, "duration", null);
        weatherInfo.temperatureChange = getIntInterval(value, "temperatureChange", null);

        if (!value.get("conditions").isnil()) {
            weatherInfo.condition = new WeatherInfo.WeatherCondition();
            if (!value.get("conditions").get("temperature").isnil()) {
                weatherInfo.condition.temperature = getIntInterval(value.get("conditions"), "temperature", null);
            }
        }

        if (!value.get("sun").isnil()) {
            weatherInfo.sun = new WeatherInfo.WeatherSunModel();
            weatherInfo.sun.dawn = getInt(value.get("sun"), "dawn", 5);
            weatherInfo.sun.noon = getInt(value.get("sun"), "noon", 6);
            weatherInfo.sun.twilight = getInt(value.get("sun"), "twilight", 18);
            weatherInfo.sun.midnight = getInt(value.get("sun"), "midnight", 19);
        }
    }
}
