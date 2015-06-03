package org.smallbox.faraway.model;

public class PlanetModel {
	public static class PlanetStatsModel {
		public int water;
		public int fertility;
		public int atmosphere;
		public int fauna;
		public int flora;
		public int hostile_fauna;
		public int hostile_humankind;
		public int hostile_mechanic;
	}

    public static class PlanetImageModel {
        public String thumb;
    }

    public static class PlanetCreditModel {
        public String author;
        public String site;
    }

	public String	            name;
	public String	            type;
	public String	            desc;
	public PlanetStatsModel     stats;
	public PlanetImageModel     image;
	public PlanetCreditModel    credit;
	public double	            albedo;
	public double 	            pressure;
	public double 	            greenhouse;
	public double 	            incomingEnergy;
}
