package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.engine.ui.OnClickListener;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.View;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.ui.UserInteraction.Action;
import org.smallbox.faraway.ui.UserInterface;

public class PanelPlan extends BaseRightPanel {
	public enum Planning {
		GATHER, MINING, DUMP, PICK, NONE
	}

	private static class PanelEntry {
		public GameEventListener.Key				shortcut;
		public String 			label;
		public OnClickListener	clickListener;
		public TextView 		view;
		
		public PanelEntry(String label, GameEventListener.Key shortcut, OnClickListener clickListener) {
			this.label = label;
			this.shortcut = shortcut;
			this.clickListener = clickListener;
		}
	}
	
	private PanelEntry[]			_entries = new PanelEntry[] {
			new PanelEntry(Strings.LB_GATHER.toUpperCase(), GameEventListener.Key.G, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.GATHER);
				}
			}),
			new PanelEntry(Strings.LB_MINING.toUpperCase(), GameEventListener.Key.M, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.MINING);
				}
			}),
			new PanelEntry(Strings.LB_DUMP.toUpperCase(), GameEventListener.Key.D, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.DUMP);
				}
			}),
			new PanelEntry(Strings.LB_PICK.toUpperCase(), GameEventListener.Key.P, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.PICK);
				}
			})
	};

	public PanelPlan(UserInterface.Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate(LayoutFactory factory) {
		int i = 0;
		for (PanelEntry entry: _entries) {
			entry.view = ViewFactory.getInstance().createTextView(150, 36);
			entry.view.setString(entry.label);
			entry.view.setPadding(3, 16);
			entry.view.setPosition(20, 20 + i++ * 50);
			entry.view.setCharacterSize(FONT_SIZE_TITLE);
			entry.view.setOnClickListener(entry.clickListener);
			entry.view.setShortcut(0);
			addView(entry.view);
		}
	}

	protected void clickOnIcon(View view) {
		for (PanelEntry entry: _entries) {
			entry.view.setBackgroundColor(new Color(29, 85, 96, 100));
			entry.view.setBorderColor(null);
		}
		view.setBackgroundColor(new Color(29, 85, 96));
		view.setBorderColor(new Color(161, 255, 255));
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible == false) {
			for (PanelEntry entry: _entries) {
				entry.view.setBackgroundColor(new Color(29, 85, 96, 100));
				entry.view.setBorderColor(null);
			}
		}
	}
	
	@Override
	public boolean	onKey(GameEventListener.Key key) {
		for (PanelEntry entry: _entries) {
			if (entry.shortcut == key) {
				entry.clickListener.onClick(entry.view);
				return true;
			}
		}
		return false;
	}
}
