package org.smallbox.faraway.core.util;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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

    public static int getStorageMaxQuantity(ItemInfo itemInfo) {
        return Math.max(Application.configurationManager.game.storageMaxQuantity, itemInfo.stack);
    }

    public static int getInventoryMaxQuantity(ItemInfo itemInfo) {
        return Math.max(Application.configurationManager.game.inventoryMaxQuantity, itemInfo.stack);
    }

    public static JSONObject toJSON(FileInputStream fis) throws IOException {
        return new JSONObject(IOUtils.toString(fis, StandardCharsets.UTF_8));
    }

    public static String getDateStr(long time) {
        int totalHours = (int)(time / Application.gameManager.getGame().getTickPerHour());
        int totalDays = totalHours / Application.gameManager.getGame().getHourPerDay();
        return "Day " + String.valueOf(totalDays) + " - " + String.valueOf(totalHours % Application.gameManager.getGame().getHourPerDay()) + "h";
    }

    public static String getTimeStr(long time) {
        int totalHours = (int)(time / Application.gameManager.getGame().getTickPerHour());
        return String.valueOf(totalHours % Application.gameManager.getGame().getHourPerDay()) + "h";
    }
}