package org.smallbox.faraway.util;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.Data;

public class StringUtils {

    public static String getDashedString(String label, String value, int columns) {
        Data data = DependencyManager.getInstance().getDependency(Data.class);

        int hash = label.hashCode();
        label = data != null && data.hasString(hash) ? data.getString(hash) : label;

        StringBuilder sb = new StringBuilder();
        sb.append(label);

        int valueHash = value.hashCode();
        value = data != null && data.hasString(valueHash) ? data.getString(valueHash) : value;

        sb.append(".".repeat(Math.max(0, columns - label.replace("_", "").length() - value.length())));

        sb.append(value);

        return sb.toString();
    }

}
