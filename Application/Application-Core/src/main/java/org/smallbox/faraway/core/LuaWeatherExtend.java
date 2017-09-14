package org.smallbox.faraway.core;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.smallbox.faraway.common.DataAsyncListener;
import org.smallbox.faraway.common.ModuleBase;
import org.smallbox.faraway.common.lua.data.DataExtendException;
import org.smallbox.faraway.common.lua.data.LuaExtend;
import org.smallbox.faraway.common.modelInfo.WeatherInfo;

import java.io.File;

/**
 * Created by Alex on 29/09/2015.
 */
public class LuaWeatherExtend extends LuaExtend {

    @Override
    public boolean accept(String type) { return "weather".equals(type); }

    @Override
    public void extend(ModuleBase module, Globals globals, LuaValue value, File dataDirectory) throws DataExtendException {
        String name = getString(value, "name", null);

        WeatherInfo weatherInfo = Application.data.weathers.get(name);
        if (weatherInfo == null) {
            weatherInfo = new WeatherInfo();
            Application.data.weathers.put(name, weatherInfo);
            Application.data.add(name, weatherInfo);
        }

        readWeather(weatherInfo, value);

//        Log.info("Extends weather from lua: " + weatherInfo.label);
    }

    private void readWeather(WeatherInfo weatherInfo, LuaValue value) throws DataExtendException {
        weatherInfo.name = getString(value, "name", null);
        weatherInfo.label = getString(value, "label", null);
        weatherInfo.icon = getString(value, "icon", null);
        weatherInfo.particle = value.get("particle").optjstring(null);
        weatherInfo.duration = getIntInterval(value, "duration", null);
        weatherInfo.temperatureChange = getIntInterval(value, "temperatureChange", null);
        weatherInfo.color1 = getInt(value, "color1", 0);
        weatherInfo.color2 = getInt(value, "color2", 0);

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

    @Override
    protected <T> void getAsync(String itemName, Class<T> cls, DataAsyncListener<T> dataAsyncListener) {
        Application.data.getAsync(itemName, cls, dataAsyncListener);
    }
}
