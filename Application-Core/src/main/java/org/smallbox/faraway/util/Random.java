package org.smallbox.faraway.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class Random {

    public static <T> T of(Map<?, T> map) {
        return map.values().stream().skip(RandomUtils.nextInt(0, map.size() - 1)).findAny().orElse(null);
    }

    public static <T> Optional<T> ofNullable(Map<?, T> map) {
        return Optional.ofNullable(of(map));
    }

    public static <T> T of(Collection<T> collection) {
        return collection.stream().skip(RandomUtils.nextInt(0, collection.size() - 1)).findAny().orElse(null);
    }

    public static <T> Optional<T> ofNullable(Collection<T> collection) {
        return Optional.ofNullable(of(collection));
    }

    public static int interval(int[] interval) {
        if (interval[0] == interval[1]) {
            return interval[0];
        }
        return (int)(Math.random() * (interval[1] - interval[0]) + interval[0]);
    }

    public static double gaussianInterval(double interval) {
        return Math.min(interval, Math.max(-interval, new java.util.Random().nextGaussian()));
    }
}
