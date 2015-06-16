package org.smallbox.faraway.model;

import java.util.List;

/**
 * Created by Alex on 14/06/2015.
 */
public class BuffModel {
    public static class BuffConditionModel {
        public boolean              previous;
        public int                  minCharacterTemperature = Integer.MIN_VALUE;
        public int                  maxCharacterTemperature = Integer.MIN_VALUE;
        public int                  minFood = Integer.MIN_VALUE;
        public int                  maxFood = Integer.MIN_VALUE;
        public int                  minEnvironment = Integer.MIN_VALUE;
        public int                  maxEnvironment = Integer.MIN_VALUE;
        public int                  minDay = Integer.MIN_VALUE;
        public int                  maxDay = Integer.MIN_VALUE;
        public int                  minSocial = Integer.MIN_VALUE;
        public int                  maxSocial = Integer.MIN_VALUE;
        public int                  minLight = Integer.MIN_VALUE;
        public int                  maxLight = Integer.MIN_VALUE;
        public int                  minOxygen = Integer.MIN_VALUE;
        public int                  maxOxygen = Integer.MIN_VALUE;
    }

    public static class BuffEffectModel {
        public int                  mood;
        public double               fainting;
        public double               death;
    }

    public static class BuffLevelModel {
        public String               label;
        public String               disease;
        public BuffConditionModel   conditions;
        public BuffEffectModel      effects;
        public double               delay;
        public int                  index;
    }

    public String                   name;
    public List<BuffLevelModel>     levels;
}
