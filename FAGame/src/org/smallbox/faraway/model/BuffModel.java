package org.smallbox.faraway.model;

import java.util.List;

/**
 * Created by Alex on 14/06/2015.
 */
public class BuffModel {
    public static class BuffConditionModel {
        public int                  minCharacterTemperature = Integer.MIN_VALUE;
        public int                  maxCharacterTemperature = Integer.MIN_VALUE;
        public int                  minFood = Integer.MIN_VALUE;
        public int                  maxFood = Integer.MIN_VALUE;
        public int                  minDay = Integer.MIN_VALUE;
        public int                  maxDay = Integer.MIN_VALUE;
    }

    public static class BuffEffectModel {
        public int                  mood;
        public double               fainting;
    }

    public static class BuffLevelModel {
        public String               label;
        public String               disease;
        public BuffConditionModel   conditions;
        public BuffEffectModel      effects;
        public int                  delay;
        public int                  index;
    }

    public String                   name;
    public List<BuffLevelModel>     levels;
}
