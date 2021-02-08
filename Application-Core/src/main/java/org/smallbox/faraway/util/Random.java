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

}
