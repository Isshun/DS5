package org.smallbox.faraway.game.planet;

import org.smallbox.faraway.game.weather.WeatherInfo;

import java.util.List;

public class RegionInfo {

    public static class RegionDistribution {
        public int[]                latitude;
        public double               frequency;
    }

    public static class RegionTerrain {
        public String               liquid;
        public String               ground;
        public String               resource;
        public String               pattern;
        public String               condition;
        public int[]                quantity;
    }

    public static class RegionMonth {
        public int                  index;
        public int                  rain;
        public int[]                temperature;
        public int[]                temperatureHourlyVariations;
    }

    public static class RegionSeason {
        public String               id;
        public int                  from;
        public int                  to;
        public int                  dayOfMonth;
        public String               name;
    }

    public static class RegionWeather {
        public WeatherInfo          info;
        public double[]             frequency;
        public double[]             duration;
    }

    public static class RegionFauna {
        public String               name;
        public String               group;
        public double               frequency;
        public int[]                number;
    }

    public PlanetInfo               planet;
    public String                   name;
    public String                   label;
    public int                      color;
    public int                      hostility;
    public int                      fertility;
    public List<RegionDistribution> spots;
    public List<RegionTerrain>      terrains;
    public List<RegionWeather>      weather;
    public List<RegionFauna>        fauna;
    public List<RegionSeason>       seasons;
    public List<RegionMonth>        months;
}