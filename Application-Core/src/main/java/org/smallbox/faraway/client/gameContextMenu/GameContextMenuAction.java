package org.smallbox.faraway.client.gameContextMenu;

import org.smallbox.faraway.game.world.Parcel;

public interface GameContextMenuAction {
    String getLabel();
    boolean check(Parcel parcel);
    Runnable getRunnable(Parcel parcel, int mouseX, int mouseY);
}
