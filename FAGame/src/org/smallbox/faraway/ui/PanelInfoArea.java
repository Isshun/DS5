package org.smallbox.faraway.ui;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.ParcelModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.panel.BaseInfoRightPanel;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoArea extends BaseInfoRightPanel {

    public PanelInfoArea(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_area.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        super.onLayoutLoaded(layout);
    }

    public void select(AreaModel area) {
        ((TextView)findById("lb_area")).setString(area.getName());
    }
}
