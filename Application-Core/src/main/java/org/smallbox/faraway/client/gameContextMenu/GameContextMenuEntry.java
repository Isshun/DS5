package org.smallbox.faraway.client.gameContextMenu;

import org.smallbox.faraway.util.log.Log;

import java.util.Optional;

public class GameContextMenuEntry {
    public final static int ENTRY_HEIGHT = 30;
    private final static int FONT_WIDTH = 14;

    private final Runnable runnable;
    private final String label;
    private final int width;
    private final int x;
    private final int y;

    public GameContextMenuEntry(String label, int x, int y, Runnable runnable) {
        this.label = label;
        this.width = label.length() * FONT_WIDTH;
        this.runnable = runnable;
        this.x = x;
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean contains(int checkX, int checkY) {
        return checkX >= x && checkX < x + width && checkY >= y && checkY < y + ENTRY_HEIGHT;
    }

    public void action() {
        Optional.ofNullable(runnable).ifPresent(Runnable::run);
        Log.info(label);
    }
}
