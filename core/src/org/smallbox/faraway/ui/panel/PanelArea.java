package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.model.area.AreaType;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.View;

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
    public void onLayoutLoaded(LayoutModel layout, FrameLayout panel) {
        findById("bt_area_storage").setOnClickListener(view -> select(UserInteraction.Action.SET_AREA, view, AreaType.STORAGE));
        findById("bt_area_remove_storage").setOnClickListener(view -> select(UserInteraction.Action.REMOVE_AREA, view, AreaType.STORAGE));
        findById("bt_area_home").setOnClickListener(view -> select(UserInteraction.Action.SET_AREA, view, AreaType.HOME));
        findById("bt_area_remove_home").setOnClickListener(view -> select(UserInteraction.Action.REMOVE_AREA, view, AreaType.HOME));
        findById("bt_area_garden").setOnClickListener(view -> select(UserInteraction.Action.SET_AREA, view, AreaType.GARDEN));
        findById("bt_area_remove_garden").setOnClickListener(view -> select(UserInteraction.Action.REMOVE_AREA, view, AreaType.GARDEN));
        findById("bt_area_dump").setOnClickListener(view -> select(UserInteraction.Action.SET_AREA, view, null));
        findById("bt_area_safe").setOnClickListener(view -> select(UserInteraction.Action.SET_AREA, view, AreaType.SAFE));
    }

    private void select(UserInteraction.Action action, View selectedView, AreaType type) {
        for (View view: ((FrameLayout)findById("frame_entries")).getViews()) {
            view.setBackgroundColor(COLOR_DEFAULT);
        }
        selectedView.setBackgroundColor(COLOR_SELECTED);

        // Set onAction
        _interaction.set(action, type);
    }
}
