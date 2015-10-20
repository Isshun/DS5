package org.smallbox.faraway.core.data.factory.world;

/**
 * Created by Alex on 06/07/2015.
 */
public class WorldFactoryConfig {
    public static WorldFactoryConfig[] CONFIGS = new WorldFactoryConfig[] {
            new WorldFactoryConfig("valley", 5, 8, 8, 2f, 0.65f),
            new WorldFactoryConfig("mountain", 5, 8, 8, 2f, 0.55f),
            new WorldFactoryConfig("mineral_common_light", 2, 128, 128, 2f, 0.65f),
            new WorldFactoryConfig("mineral_common_large", 4, 64, 64, 2f, 0.6f),
            new WorldFactoryConfig("mineral_rare_light", 2, 128, 128, 2f, 0.75f),
            new WorldFactoryConfig("mineral_rare_large", 4, 16, 16, 2.5f, 0.7f),
    };

    public final int n;
    public final int w;
    public final int h;
    public final String name;
    public final float threshold;
    public final float smooth;

    public WorldFactoryConfig(String name, int n, int w, int h, float smooth, float threshold) {
        this.name = name;
        this.n = n;
        this.w = w;
        this.h = h;
        this.threshold = threshold;
        this.smooth = smooth;
    }

    public static WorldFactoryConfig get(String name) {
        for (WorldFactoryConfig config: CONFIGS) {
            if (config.name.equals(name)) {
                return config;
            }
        }
        return null;
    }
}
