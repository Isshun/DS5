package org.smallbox.faraway.ui;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.ui.panel.BasePanel;
import org.smallbox.faraway.ui.panel.BaseRightPanel;

/**
 * Created by Alex on 13/06/2015.
 */
public class PanelArea extends BaseRightPanel {
    private static final Color COLOR_SELECTED = new Color(0xb0cd35);
    private static final Color COLOR_DEFAULT = new Color(0x298596);

    public PanelArea(UserInterface.Mode mode, GameEventListener.Key shortcut) {
        super(mode, shortcut, "data/ui/panels/areas.yml");
    }

    @Override
    public void onLayoutLoaded(LayoutModel layout) {
        findById("bt_area_storage").setOnClickListener(view -> select(view));
        findById("bt_area_home").setOnClickListener(view -> select(view));
        findById("bt_area_dump").setOnClickListener(view -> select(view));
        findById("bt_area_safe").setOnClickListener(view -> select(view));
    }

    private void select(View selectedView) {
        for (View view: ((FrameLayout)findById("frame_entries")).getViews()) {
            view.setBackgroundColor(COLOR_DEFAULT);
        }
        selectedView.setBackgroundColor(COLOR_SELECTED);

        // Set action
        _interaction.set(UserInteraction.Action.SET_AREA, AreaType.STORAGE);
    }
}
