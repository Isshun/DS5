package org.smallbox.faraway.client.gameContextMenu;

import org.smallbox.faraway.core.module.world.model.ParcelModel;

public interface GameContextMenuAction {
    String getLabel();
    boolean check(ParcelModel parcel, int mouseX, int mouseY);
    Runnable getRunnable(ParcelModel parcel, int mouseX, int mouseY);
}
