package org.smallbox.faraway.util;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.smallbox.faraway.core.game.Game;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static JSONObject toJSON(FileInputStream fis) throws IOException {
        return new JSONObject(IOUtils.toString(fis, StandardCharsets.UTF_8));
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

    public static void requireNonNull(Object object, Class<?> cls, String message) {
        if (object == null) {
            throw new GameException(cls, message);
        }
    }
}