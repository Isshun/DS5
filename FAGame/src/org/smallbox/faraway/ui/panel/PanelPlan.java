package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.engine.ui.FrameLayout;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.ui.LayoutModel;
import org.smallbox.faraway.ui.UserInteraction.Action;
import org.smallbox.faraway.ui.UserInterface;

public class PanelPlan extends BaseRightPanel {
	public enum Planning {
		GATHER, MINING, DUMP, PICK, NONE
	}

	public PanelPlan(UserInterface.Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut, "data/ui/panels/plan.yml");
	}

	@Override
	public void onLayoutLoaded(LayoutModel layoutModel) {
		findById("bt_mine").setOnClickListener(view -> select(view, Planning.MINING));
		findById("bt_gather").setOnClickListener(view -> select(view, Planning.GATHER));
		findById("bt_dump").setOnClickListener(view -> select(view, Planning.DUMP));
		findById("bt_cut").setOnClickListener(view -> select(view, Planning.NONE));
	}

	private void select(View view, Planning planning) {
		// Activate button
		for (View v: ((FrameLayout)findById("frame_entries")).getViews()) {
			v.setBackgroundColor(new Color(0, 85, 96));
		}
		view.setBackgroundColor(new Color(255, 85, 96));

		// Set action
		_interaction.set(Action.SET_PLAN, planning);
	}

//	@Override
//	public void setVisible(boolean isVisible) {
//		super.setVisible(isVisible);
//		if (isVisible == false) {
//			for (PanelEntry entry: _entries) {
//				entry.view.setBackgroundColor(new Color(29, 85, 96, 100));
//				entry.view.setBorderColor(null);
//			}
//		}
//	}
	
//	@Override
//	public boolean	onKey(GameEventListener.Key key) {
//		for (PanelEntry entry: _entries) {
//			if (entry.shortcut == key) {
//				entry.clickListener.onClick(entry.view);
//				return true;
//			}
//		}
//		return false;
//	}
}
