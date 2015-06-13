package org.smallbox.faraway.model;

/**
 * Created by Alex on 05/06/2015.
 */
public class WeatherModel {
    public static class WeatherSunModel {
        public int          dawn;
        public int          noon;
        public int          midnight;
        public int          twilight;
    }

    public static class WeatherCondition {
        public int[]        temperature;
    }

    public String           name;
    public String           particle;
    public WeatherSunModel  sun;
    public WeatherCondition condition;
    public boolean          unique;
    public int[]            temperatureChange;
    public int[]            duration;
}
