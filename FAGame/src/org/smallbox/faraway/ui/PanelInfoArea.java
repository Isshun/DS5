package org.smallbox.faraway.ui;

import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.engine.TextView;
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
        findById("bt_remove_area").setOnClickListener(view -> Game.getAreaManager().remove(area));
    }
}
