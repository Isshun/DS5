package org.smallbox.faraway.ui.panel.info;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoParcel extends BaseInfoRightPanel {

    public PanelInfoParcel(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_parcel.yml");
    }

    public void select(ParcelModel parcel) {
        super.select(parcel);
    }
}
