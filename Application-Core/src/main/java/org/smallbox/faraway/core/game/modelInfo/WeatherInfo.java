package org.smallbox.faraway.core.game.modelInfo;

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
    public String           music;
    public int              color1;
    public int              color2;
    public WeatherSunModel  sun;
    public WeatherCondition condition;
    public boolean          unique;
    public int[]            temperatureChange;
    public int[]            duration;
}
