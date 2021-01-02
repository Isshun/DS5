package org.smallbox.faraway.client.gameContextMenu;

import java.util.ArrayList;
import java.util.List;

public class GameContextMenu {
    private final static int OFFSET_X = 15;
    private final static int OFFSET_Y = 10;

    private final List<GameContextMenuEntry> entries = new ArrayList<>();
    private final int x;
    private final int y;
    private int index;

    public GameContextMenu(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addEntry(String label, int mouseX, int mouseY, Runnable runnable) {
        entries.add(new GameContextMenuEntry(label, mouseX + OFFSET_X, mouseY + OFFSET_Y + GameContextMenuEntry.ENTRY_HEIGHT * index++, runnable));
    }

    public List<GameContextMenuEntry> getEntries() {
        return entries;
    }
}
