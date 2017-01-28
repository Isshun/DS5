package org.smallbox.faraway.util;

import java.util.Collection;
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

    public static boolean notContains(Collection<?> collection, Object object) {
        return collection == null || !collection.contains(object);
    }
}
