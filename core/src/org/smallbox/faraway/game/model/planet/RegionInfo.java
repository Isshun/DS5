package org.smallbox.faraway.game.model.planet;

import java.util.List;

/**
 * Created by Alex on 26/06/2015.
 */
public class RegionInfo {
    public static class RegionDistribution {
        public int[]				latitude;
        public double 				frequency;
    }

    public static class RegionTerrain {
        public String               type;
        public int                  typeId;
        public String 				resource;
        public String 				pattern;
        public String               condition;
        public int[]                quantity;
    }

    public static class RegionWeather {
        public String 				name;
        public double[] 			frequency;
        public double[]				duration;
    }

    public static class RegionFauna {
        public String 				name;
        public String 				group;
        public double 				frequency;
        public int[]				number;
    }

    public PlanetInfo               planet;
    public String	    			name;
    public String	        		label;
    public int                      color;
    public List<RegionDistribution> distribution;
    public List<RegionTerrain>		terrains;
    public List<RegionWeather>		weather;
    public List<RegionFauna>		fauna;
    public int[]                    temperature;
}
