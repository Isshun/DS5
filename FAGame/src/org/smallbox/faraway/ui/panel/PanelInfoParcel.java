package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.TextView;
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

        ((TextView)findById("lb_name")).setString("Ground");

        ((TextView)findById("lb_blood")).setString("blood: " + parcel.getBlood());
        ((TextView)findById("lb_dirt")).setString("dirt: " + parcel.getDirt());
        ((TextView)findById("lb_rubble")).setString("rubble: " + parcel.getRubble());
        ((TextView)findById("lb_snow")).setString("snow: " + parcel.getSnow());
    }
}
