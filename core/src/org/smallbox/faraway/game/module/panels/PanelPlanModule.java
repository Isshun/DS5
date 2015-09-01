package org.smallbox.faraway.game.module.panels;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInteraction;
import org.smallbox.faraway.ui.UserInteraction.Action;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.ui.panel.BaseRightPanel;

public class PanelPlanModule extends GameUIModule {
	public enum Planning {
		GATHER, MINING, DUMP, PICK, NONE, CUT, CUT_PLANT, HAUL
	}

	private class PanelPlanModuleWindow extends UIWindow {
		@Override
		protected void onCreate(UIWindow window, FrameLayout content) {
			findById("bt_mine").setOnClickListener(view -> select(view, Planning.MINING));
			findById("bt_gather").setOnClickListener(view -> select(view, Planning.GATHER));
			findById("bt_dump").setOnClickListener(view -> select(view, Planning.DUMP));
			findById("bt_cut").setOnClickListener(view -> select(view, Planning.CUT));
			findById("bt_haul").setOnClickListener(view -> select(view, Planning.HAUL));
			findById("bt_cut_plant").setOnClickListener(view -> select(view, Planning.CUT_PLANT));
		}

		@Override
		protected void onRefresh(int update) {
		}

		@Override
		protected String getContentLayout() {
			return "panels/plan";
		}

        private void select(View view, Planning planning) {
            ((FrameLayout)findById("frame_entries")).getViews().forEach(v -> v.setBackgroundColor(new Color(0, 85, 96)));

            // Activate button
            view.setBackgroundColor(new Color(176, 205, 53));

            // Set onAction
            UserInterface.getInstance().getInteraction().set(Action.SET_PLAN, planning);
        }
	}

	@Override
	protected void onLoaded() {
        ((PanelModule)ModuleManager.getInstance().getModule(PanelModule.class)).addShortcut("Plan", new PanelPlanModuleWindow());
	}

	@Override
	protected void onUpdate(int tick) {
	}
}
