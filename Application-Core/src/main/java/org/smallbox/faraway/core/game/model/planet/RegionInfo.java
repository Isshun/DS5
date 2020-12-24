package org.smallbox.faraway.core.game.model.planet;

import org.smallbox.faraway.core.game.modelInfo.WeatherInfo;

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

    public static class RegionTemperature {
        public int                  fromFloor;
        public int                  toFloor;
        public double[]             temperature;
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
    public List<RegionDistribution> spots;
    public List<RegionTerrain>      terrains;
    public List<RegionWeather>      weather;
    public List<RegionFauna>        fauna;
    public List<RegionTemperature>  temperatures;
}