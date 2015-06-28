package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.ResourceManager;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.engine.UILabel;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.util.Constant;

public class PanelResource extends BasePanel {
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 	FRAME_HEIGHT = 32;

	private UILabel _spice;
	private UILabel _energy;
	private UILabel _matter;
	private UILabel _o2;
	private ResourceManager _resourceManager;

	public PanelResource() {
		super(Mode.NONE, null, Constant.WINDOW_WIDTH - FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT, null);
	}

	@Override
	protected void onCreate(ViewFactory viewFactory) {
		_resourceManager = (ResourceManager) Game.getInstance().getManager(ResourceManager.class);

		_spice = viewFactory.createTextView(10, 10);
		_spice.setCharacterSize(14);
		_spice.setColor(Color.WHITE);
		_spice.setPosition(10, 6);
		addView(_spice);
		
		_energy = viewFactory.createTextView(10, 10);
		_energy.setCharacterSize(14);
		_energy.setColor(Color.WHITE);
		_energy.setPosition(110, 6);
		addView(_energy);

		_matter = viewFactory.createTextView(10, 10);
		_matter.setCharacterSize(14);
		_matter.setColor(Color.WHITE);
		_matter.setPosition(210, 6);
		addView(_matter);

		_o2 = viewFactory.createTextView(10, 10);
		_o2.setCharacterSize(14);
		_o2.setColor(Color.WHITE);
		_o2.setPosition(332, 6);
		addView(_o2);
		
		setAlwaysVisible(true);
	}
	
	@Override
	public void onRefresh(int frame) {
        _spice.setString("Food: " + String.valueOf(_resourceManager.getFood().value));
        _o2.setString("O2: " + String.valueOf(_resourceManager.getO2().value));
        _energy.setString("PW: " + String.valueOf(_resourceManager.getPower().value));
        _matter.setString("M: " + String.valueOf(_resourceManager.getScience().value));
	}
}
