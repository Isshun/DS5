package org.smallbox.faraway.util;

import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.game.DataManager;

public class StringUtils {

    public static String getDashedString(String label, String value, int columns) {
        DataManager dataManager = DependencyManager.getInstance().getDependency(DataManager.class);

        int hash = label.hashCode();
        label = dataManager != null && dataManager.hasString(hash) ? dataManager.getString(hash) : label;

        StringBuilder sb = new StringBuilder();
        sb.append(label);

        int valueHash = value.hashCode();
        value = dataManager != null && dataManager.hasString(valueHash) ? dataManager.getString(valueHash) : value;

        sb.append(".".repeat(Math.max(0, columns - label.replace("_", "").length() - value.length())));

        sb.append(value);

        return sb.toString();
    }

}
