package org.smallbox.faraway.util;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.GameManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static long getLastDataModified() {
        long lastModified = 0;

        for (File file: FileUtils.listRecursively(FileUtils.getDataFile("modules"))) {
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

    public static JSONObject toJSON(FileInputStream fis) throws IOException {
        return new JSONObject(IOUtils.toString(fis, StandardCharsets.UTF_8));
    }

    public static String getDateStr(long time) {
        int totalHours = (int)(time / DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getTickPerHour());
        int totalDays = totalHours / DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getHourPerDay();
        return "Day " + String.valueOf(totalDays) + " - " + String.valueOf(totalHours % DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getHourPerDay()) + "h";
    }

    public static String getTimeStr(long time) {
        int totalHours = (int)(time / DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getTickPerHour());
        return String.valueOf(totalHours % DependencyInjector.getInstance().getDependency(GameManager.class).getGame().getHourPerDay()) + "h";
    }

    public static double bound(double min, double max, double value) {
        return Math.max(min, Math.min(max, value));
    }

    public static int bound(int min, int max, int value) {
        return Math.max(min, Math.min(max, value));
    }

    public static int round(double value, int round) {
        return (int) (Math.floor(value / round) * round);
    }

    public static double progress(int from, int to, int current) {
        return (double)(current - from) / (to - from);
    }

    public static long day(int value) {
        return value * 24 * 60 * 60 * 1000;
    }

    public static long hour(int value) {
        return value * 60 * 60 * 1000;
    }

    public static long minute(int value) {
        return value * 60 * 1000;
    }

    public static long second(int value) {
        return value * 1000;
    }
}