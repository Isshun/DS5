package org.smallbox.faraway.core.util;

import org.smallbox.faraway.core.game.module.world.model.ParcelModel;

import java.io.File;

public class Utils {

    private static int _uuid;

    public static int getUUID() {
        return ++_uuid;
    }

    public static void useUUID(int usedId) {
        if (_uuid < usedId + 1) {
            _uuid = usedId + 1;
        }
    }

    public static long getLastUIModified() {
        long lastModified = 0;

        for (File file: new File("data/ui/").listFiles()) {
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    if (subFile.lastModified() > lastModified) {
                        lastModified = subFile.lastModified();
                    }
                }
            }
            if (file.lastModified() > lastModified) {
                lastModified = file.lastModified();
            }
        }

        for (File file: FileUtils.listRecursively("data/modules/")) {
            if (file.lastModified() > lastModified) {
                lastModified = file.lastModified();
            }
        }

        for (File file: new File("data/lang/").listFiles()) {
            if (file.lastModified() > lastModified) {
                lastModified = file.lastModified();
            }
        }

        return lastModified;
    }

    public static int getDistance(ParcelModel parcel, int x, int y) {
        return Math.abs(parcel.x - x) + Math.abs(parcel.y - y);
    }

    public static int getDistance(ParcelModel p1, ParcelModel p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public static int getRandom(int[] interval) {
        if (interval[0] == interval[1]) {
            return interval[0];
        }
        return (int)(Math.random() * (interval[1] - interval[0]) + interval[0]);
    }
}
