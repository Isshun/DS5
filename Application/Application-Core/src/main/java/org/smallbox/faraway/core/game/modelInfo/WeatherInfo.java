package org.smallbox.faraway.core.game.modelInfo;

/**
 * Created by Alex on 05/06/2015.
 */
public class WeatherInfo extends ObjectInfo {
    public static class WeatherSunModel {
        public int          dawn;
        public int          noon;
        public int          midnight;
        public int          twilight;
    }

    public static class WeatherCondition {
        public int[]        temperature;
    }

    public String           label;
    public String           particle;
    public String           icon;
    public WeatherSunModel  sun;
    public WeatherCondition condition;
    public boolean          unique;
    public int[]            temperatureChange;
    public int[]            duration;
}
