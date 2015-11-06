package org.smallbox.faraway.core.game.model.planet;

import org.smallbox.faraway.core.game.model.ObjectInfo;

import java.util.List;

public class PlanetInfo extends ObjectInfo {

    public static class PlanetFarming {
        public int[]    growing;
    }

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

    public static class PlanetGraphicInfo {
        public String       path;
    }

    public static class PlanetGraphicsInfo {
        public PlanetGraphicInfo    thumb;
        public PlanetGraphicInfo    background;
    }

    public static class PlanetImage {
        public String                     thumb;
    }

    public static class PlanetCredit {
        public String                     author;
        public String                     site;
    }

    public static class DayTime {
        public int                      hour;
        public int                      duration;
        public double                   light;
        public String                   sun;
    }

    public List<DayTime>                dayTimes;
    public String                        label;
    public String cls;
    public String                        desc;
    public PlanetStats                     stats;
    public PlanetImage                     image;
    public PlanetCredit                 credit;
    public List<RegionInfo>             regions;
    public PlanetFarming                 farming;
    public double                        albedo;
    public double                         pressure;
    public double                         greenhouse;
    public double                         incomingEnergy;
    public PlanetGraphicsInfo           graphics = new PlanetGraphicsInfo();

    public int                             dayDuration = 24;
    public int                             yearDuration = 365;
}