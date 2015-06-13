package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.ParcelModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoParcel extends BaseInfoRightPanel {
    private TextView _lbName;

    public PanelInfoParcel(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_parcel.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        super.onLayoutLoaded(layout);

        _lbName = (TextView)findById("lb_name");
    }

    public void select(ParcelModel area) {
        super.select(area);

        _lbName.setString("Ground");
    }
}
