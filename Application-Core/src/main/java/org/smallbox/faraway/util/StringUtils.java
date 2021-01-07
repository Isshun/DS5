package org.smallbox.faraway.util;

import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.game.Data;

public class StringUtils {

    public static String getDashedString(String label, String value, int columns) {
        Data data = DependencyInjector.getInstance().getDependency(Data.class);

        int hash = label.hashCode();
        label = data != null && data.hasString(hash) ? data.getString(hash) : label;

        StringBuilder sb = new StringBuilder();
        sb.append(label);

        int valueHash = value.hashCode();
        value = data != null && data.hasString(valueHash) ? data.getString(valueHash) : value;

        for (int i = columns - label.replace("_", "").length() - value.length(); i > 0; i--) {
            sb.append('.');
        }

        sb.append(value);

        return sb.toString();
    }

    public static String getPlanetStatsText(int level) {
        switch (level) {
            case -3: return "non-existent";
            case -2: return "rare";
            case -1: return "low";
            default: return "regular";
            case 1: return "abundant";
            case 2: return "extreme";
        }
    }

    public static String getPlanetStatsSymbol(int level) {
        switch (level) {
            case -3: return "---";
            case -2: return "--";
            case -1: return "-";
            default: return "+";
            case 1: return "++";
            case 2: return "+++";
        }
    }

    public static double formatNumber(double value) {
        return ((int)(value * 100)) / 100.0;
    }
}
