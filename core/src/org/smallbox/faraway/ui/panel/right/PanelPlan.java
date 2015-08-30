package org.smallbox.faraway.ui.panel.right;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInteraction.Action;
import org.smallbox.faraway.ui.UserInterface;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.ui.panel.BaseRightPanel;

public class PanelPlan extends BaseRightPanel {
	public enum Planning {
		GATHER, MINING, DUMP, PICK, NONE, CUT, CUT_PLANT, HAUL
	}

	public PanelPlan(UserInterface.Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, "data/ui/panels/plan.yml");
	}

	@Override
	public void onLayoutLoaded(LayoutModel layoutModel, FrameLayout panel) {
		findById("bt_mine").setOnClickListener(view -> select(view, Planning.MINING));
		findById("bt_gather").setOnClickListener(view -> select(view, Planning.GATHER));
		findById("bt_dump").setOnClickListener(view -> select(view, Planning.DUMP));
		findById("bt_cut").setOnClickListener(view -> select(view, Planning.CUT));
		findById("bt_haul").setOnClickListener(view -> select(view, Planning.HAUL));
		findById("bt_cut_plant").setOnClickListener(view -> select(view, Planning.CUT_PLANT));
	}

	private void select(View view, Planning planning) {
		((FrameLayout)findById("frame_entries")).getViews().forEach(v -> v.setBackgroundColor(new Color(0, 85, 96)));

        // Activate button
		view.setBackgroundColor(new Color(176, 205, 53));

		// Set onAction
		_interaction.set(Action.SET_PLAN, planning);
	}
}
