package alone.in.deepspace.ui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInteraction.Action;

public class PanelPlan extends BaseRightPanel {
	public enum Planning {
		GATHER, MINING, DUMP, PICK, NONE
	}

	private static class PanelEntry {
		public Key				shortcut;
		public String 			label;
		public OnClickListener	clickListener;
		public ButtonView		view;
		
		public PanelEntry(String label, Key shortcut, OnClickListener clickListener) {
			this.label = label;
			this.shortcut = shortcut;
			this.clickListener = clickListener;
		}
	}
	
	private PanelEntry[]			_entries = new PanelEntry[] {
			new PanelEntry(Strings.LB_GATHER.toUpperCase(), Key.G, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.GATHER);
				}
			}),
			new PanelEntry(Strings.LB_MINING.toUpperCase(), Key.M, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.MINING);
				}
			}),
			new PanelEntry(Strings.LB_DUMP.toUpperCase(), Key.D, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.DUMP);
				}
			}),
			new PanelEntry(Strings.LB_PICK.toUpperCase(), Key.P, new OnClickListener() {
				@Override
				public void onClick(View view) {
					clickOnIcon(view);
					_interaction.set(Action.SET_PLAN, Planning.PICK);
				}
			})
	};

	public PanelPlan(UserInterface.Mode mode, Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate() {
		int i = 0;
		for (PanelEntry entry: _entries) {
			entry.view = new ButtonView(new Vector2f(150, 36));
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
	public boolean	onKey(Keyboard.Key key) {
		for (PanelEntry entry: _entries) {
			if (entry.shortcut == key) {
				entry.clickListener.onClick(entry.view);
				return true;
			}
		}
		return false;
	}
}
