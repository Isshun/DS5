package org.smallbox.faraway.client.shortcut;

public final class ShortcutStrategy {
    public final String label;
    public final int key;
    final Runnable runnable;

    ShortcutStrategy(String label, int key, Runnable runnable) {
        this.label = label;
        this.key = key;
        this.runnable = runnable;
    }
}
