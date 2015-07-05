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

    public static class RegionResource {
        public String 				name;
        public double 				frequency;
        public int[]				size;
        public int[]				quantity;
        public String				terrain;
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

    public String	    			name;
    public String	        		label;
    public int                      color;
    public List<RegionDistribution> distribution;
    public List<RegionResource>		resources;
    public List<RegionWeather>		weather;
    public List<RegionFauna>		fauna;
    public int                      temperature;
}
