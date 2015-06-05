package org.smallbox.faraway.model;

/**
 * Created by Alex on 05/06/2015.
 */
public class WeatherModel {
    public static class WeatherSunModel {
        public int dawn;
        public int noon;
        public int midnight;
        public int twilight;
    }

    public String           name;
    public String           particle;
    public WeatherSunModel  sun;
}
