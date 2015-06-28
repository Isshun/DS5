package org.smallbox.faraway.game.model.planet;

import java.util.List;

public class PlanetInfo {
	public static class PlanetStats {
		public int water;
		public int fertility;
		public int atmosphere;
		public int fauna;
		public int flora;
		public int hostile_fauna;
		public int hostile_humankind;
		public int hostile_mechanic;
	}

    public static class PlanetImage {
        public String 					thumb;
    }

    public static class PlanetCredit {
        public String 					author;
        public String 					site;
    }

	public String	            		name;
	public String	            		type;
	public String	            		desc;
	public PlanetStats 					stats;
	public PlanetImage 					image;
	public PlanetCredit 				credit;
	public List<RegionInfo> 			regions;
	public double	            		albedo;
	public double 	            		pressure;
	public double 	            		greenhouse;
	public double 	            		incomingEnergy;

	public int 							dayDuration = 24;
	public int 							yearDuration = 365;
}
