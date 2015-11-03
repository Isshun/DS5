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

    public static long getLastDataModified() {
        long lastModified = 0;

        for (File file: FileUtils.listRecursively("data/modules/")) {
            if (file.lastModified() > lastModified) {
                lastModified = file.lastModified();
            }
        }

        return lastModified;
    }

    public static int getRandom(int[] interval) {
        if (interval[0] == interval[1]) {
            return interval[0];
        }
        return (int)(Math.random() * (interval[1] - interval[0]) + interval[0]);
    }
}
