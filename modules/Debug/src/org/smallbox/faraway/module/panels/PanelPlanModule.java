package org.smallbox.faraway.module.panels;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.UserInteraction.Action;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.views.UIFrame;
import org.smallbox.faraway.ui.engine.views.View;

public class PanelPlanModule extends GameUIModule {
    private class PanelPlanModuleWindow extends UIWindow {
        @Override
        protected void onCreate(UIWindow window, UIFrame content) {
            findById("bt_mine").setOnClickListener(view -> select(view, "mining"));
            findById("bt_gather").setOnClickListener(view -> select(view, "gather"));
            findById("bt_dump").setOnClickListener(view -> select(view, "dump"));
            findById("bt_cut").setOnClickListener(view -> select(view, "cut"));
            findById("bt_haul").setOnClickListener(view -> select(view, "haul"));
        }

        @Override
        protected void onRefresh(int update) {
        }

        @Override
        protected String getContentLayout() {
            return "panels/plan";
        }

        private void select(View view, String planning) {
            ((UIFrame)findById("frame_entries")).getViews().forEach(v -> v.setBackgroundColor(new Color(0, 85, 96)));

            // Activate button
            view.setBackgroundColor(new Color(176, 205, 53));

            // Set onAction
            UserInterface.getInstance().getInteraction().set(Action.SET_PLAN, planning);
        }
    }

    @Override
    protected void onLoaded() {
//        ((PanelModule)ModuleManager.getInstance().getModule(PanelModule.class)).addShortcut("Plan", new PanelPlanModuleWindow());
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
