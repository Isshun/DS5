package org.smallbox.faraway.core.game;

import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationObject
public class MonitoringManager {
    private final static boolean ENABLE_MONITORING = true;

    public static class MonitoringManagerEntry {
        public final Object model;
        public long footPrint;
        public long duration;
        public long calls;

        public MonitoringManagerEntry(Object model) {
            this.model = model;
        }

        public String getFootPrintFormatted() {
            return (footPrint < 1000000) ? footPrint / 1000 + " ko" : footPrint / 1000000 + " mo";
        }

        public String getFootPrintFormattedByCycle() {
            long footPrintByCycle = footPrint / calls;
            return (footPrintByCycle < 1000000) ? footPrintByCycle / 1000 + " ko" : footPrintByCycle / 1000000 + " mo";
        }
    }

    public final Map<Object, MonitoringManagerEntry> entries = new ConcurrentHashMap<>();

    public Runnable encapsulateRunnable(Object model, Runnable runnable) {
        return ENABLE_MONITORING ? () -> {
            entries.computeIfAbsent(model, MonitoringManagerEntry::new);

            long footPrintBefore = Runtime.getRuntime().freeMemory();
            long timeBefore = System.currentTimeMillis();

            runnable.run();

            long footPrintAfter = Runtime.getRuntime().freeMemory();

            MonitoringManagerEntry entry = entries.get(model);
            entry.duration += (System.currentTimeMillis() - timeBefore);
            entry.footPrint += (footPrintBefore > footPrintAfter ? footPrintBefore - footPrintAfter : 0);
            entry.calls++;
        } : runnable;
    }

}
