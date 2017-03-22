package org.smallbox.faraway.util;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Alex on 12/05/2016.
 */
public class CollectionUtils {
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static <T> boolean contains(Collection<T> collection, T object) {
        return collection != null || collection.contains(object);
    }

    public static <T> boolean notContains(Collection<T> collection, T object) {
        return collection == null || !collection.contains(object);
    }

    public static boolean containsEquals(List<ParcelModel> c1, List<ParcelModel> c2) {
        return c1 != null && c2 != null && c2.containsAll(c1) && c1.containsAll(c2);
    }
}
