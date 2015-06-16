package org.smallbox.faraway.model;

import java.util.List;

/**
 * Created by Alex on 16/06/2015.
 */
public class EquipmentModel {
    public static class EquipmentEffectBuff {
        public int                      sight;
        public int                      grow;
        public int                      repair;
        public int                      build;
        public int                      craft;
        public int                      cook;
        public int                      speed;
        public int                      tailoring;
        public double                   oxygen;
    }

    public static class EquipmentEffectAbsorb {
        public int                      cold;
        public int                      heat;
        public int                      damage;
    }

    public static class EquipmentEffectResist {
        public int                      cold;
        public int                      heat;
        public int                      damage;
        public int                      oxygen;
    }

    public static class EquipmentEffectCondition {
        public int                      minSight;
        public int                      maxSight;
    }

    public static class EquipmentEffect {
        public EquipmentEffectCondition condition;
        public EquipmentEffectAbsorb    absorb;
        public EquipmentEffectResist    resist;
        public EquipmentEffectBuff      buff;
    }

    public String                       name;
    public String                       label;
    public String                       desc;
    public String                       location;
    public List<EquipmentEffect>        effects;
}
