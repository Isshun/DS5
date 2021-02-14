package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationObject
public class MonitoringManager {
    public final Map<Object, Long> memoryMap = new ConcurrentHashMap<>();

    public Runnable encapsulate(Object model, Runnable runnable) {
        return () -> {
            long before = Runtime.getRuntime().freeMemory();
            runnable.run();
            long after = Runtime.getRuntime().freeMemory();

            if (before > after) {
                memoryMap.put(model, memoryMap.getOrDefault(model, 0L) + before - after);
            }
        };
    }

}
