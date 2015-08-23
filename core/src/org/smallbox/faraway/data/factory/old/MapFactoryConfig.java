package org.smallbox.faraway.data.factory.old;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 21/06/2015.
 */
public class MapFactoryConfig {
    public void addAdjustment(float min, float max) {
        this.adjustments.add(new AdjustmentValue(min, max));
    }

    public static class AdjustmentValue {
        public float min;
        public float max;

        public AdjustmentValue(float min, float max) {
            this.min = min;
            this.max = max;
        }
    }

    public int                     perlinOctave;
    public List<AdjustmentValue> adjustments = new ArrayList<>();

    public static MapFactoryConfig createValleys() {
        MapFactoryConfig config = new MapFactoryConfig();
        config.perlinOctave = 9;
        config.addAdjustment(0.2f, 0.68f);
        config.addAdjustment(0.2f, 0.68f);
        config.addAdjustment(0.2f, 0.68f);
        config.addAdjustment(0.2f, 0.68f);
        config.addAdjustment(0.2f, 0.68f);
        return config;
    }

//    public static MapFactoryConfig createValleys() {
//        MapFactoryConfig config = new MapFactoryConfig();
//        config.perlinOctave = 9;
//        config.addAdjustment(0.2f, 0.68f);
//        config.addAdjustment(0.2f, 0.68f);
//        config.addAdjustment(0.2f, 0.68f);
//        config.addAdjustment(0.2f, 0.68f);
//        config.addAdjustment(0.2f, 0.68f);
//        return config;
//    }

    public static MapFactoryConfig createMountains() {
        MapFactoryConfig config = new MapFactoryConfig();
        config.perlinOctave = 8;
        config.addAdjustment(0.25f, 0.55f);
        return config;
    }

}
