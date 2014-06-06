package alone.in.deepspace.ui.panel;

import java.util.ArrayList;
import java.util.List;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard.Key;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.ui.UserInterface;
import alone.in.deepspace.ui.UserInteraction.Action;

public class PanelPlan extends BaseRightPanel {
	public enum Planning {
		GATHER, MINING, DUMP, NONE
	}

	private List<View> 				_buttons;

	public PanelPlan(UserInterface.Mode mode, Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate() {
		_buttons = new ArrayList<View>();
		
		ButtonView btGather = new ButtonView(new Vector2f(150, 36));
		btGather.setString(Strings.LB_GATHER.toUpperCase());
		btGather.setPadding(3, 16);
		btGather.setPosition(20, 20);
		btGather.setCharacterSize(FONT_SIZE_TITLE);
		btGather.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				clickOnIcon(view);
				_interaction.set(Action.SET_PLAN, Planning.GATHER);
			}
		});
		btGather.setShortcut(0);
		addView(btGather);
		_buttons.add(btGather);

		ButtonView btMining = new ButtonView(new Vector2f(150, 36));
		btMining.setString(Strings.LB_MINING.toUpperCase());
		btMining.setPadding(3, 16);
		btMining.setPosition(20, 70);
		btMining.setCharacterSize(FONT_SIZE_TITLE);
		btMining.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				clickOnIcon(view);
				_interaction.set(Action.SET_PLAN, Planning.DUMP);
			}
		});
		btMining.setShortcut(0);
		addView(btMining);
		_buttons.add(btMining);

		ButtonView btDump = new ButtonView(new Vector2f(150, 36));
		btDump.setString(Strings.LB_DUMP.toUpperCase());
		btDump.setPadding(3, 16);
		btDump.setPosition(20, 120);
		btDump.setCharacterSize(FONT_SIZE_TITLE);
		btDump.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				clickOnIcon(view);
				_interaction.set(Action.SET_PLAN, Planning.MINING);
			}
		});
		btDump.setShortcut(0);
		addView(btDump);
		_buttons.add(btDump);
	}

	protected void clickOnIcon(View view) {
		for (View button: _buttons) {
			button.setBackgroundColor(new Color(29, 85, 96, 100));
			button.setBorderColor(null);
		}
		view.setBackgroundColor(new Color(29, 85, 96));
		view.setBorderColor(new Color(161, 255, 255));
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible == false) {
			for (View button: _buttons) {
				button.setBackgroundColor(new Color(29, 85, 96, 100));
				button.setBorderColor(null);
			}
		}
	}
}
