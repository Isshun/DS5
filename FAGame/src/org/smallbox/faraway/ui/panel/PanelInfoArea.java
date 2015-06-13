package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.model.item.AreaModel;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 01/06/2015.
 */
public class PanelInfoArea extends BaseRightPanel {
    private TextView _lbName;

    public PanelInfoArea(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/info_area.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        _lbName = (TextView)findById("lb_name");
    }

    public void select(AreaModel area) {
        _lbName.setString("Ground");
    }
}
