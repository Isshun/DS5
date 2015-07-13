package org.smallbox.faraway.game;

/**
 * Created by Alex on 06/07/2015.
 */
public class MapGenConfig {
    public static MapGenConfig[] CONFIGS = new MapGenConfig[] {
            new MapGenConfig("valley", 5, 8, 8, 2f, 0.65f),
            new MapGenConfig("mountain", 5, 8, 8, 2f, 0.55f),
            new MapGenConfig("mineral_common_light", 2, 128, 128, 2f, 0.65f),
            new MapGenConfig("mineral_common_large", 4, 64, 64, 2f, 0.6f),
            new MapGenConfig("mineral_rare_light", 2, 128, 128, 2f, 0.75f),
            new MapGenConfig("mineral_rare_large", 4, 16, 16, 2.5f, 0.7f),
    };

    public final int n;
    public final int w;
    public final int h;
    public final String name;
    public final float threshold;
    public final float smooth;

    public MapGenConfig(String name, int n, int w, int h, float smooth, float threshold) {
        this.name = name;
        this.n = n;
        this.w = w;
        this.h = h;
        this.threshold = threshold;
        this.smooth = smooth;
    }

    public static MapGenConfig get(String name) {
        for (MapGenConfig config: CONFIGS) {
            if (config.name.equals(name)) {
                return config;
            }
        }
        return null;
    }
}
