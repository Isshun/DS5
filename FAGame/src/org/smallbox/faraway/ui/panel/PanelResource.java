package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.engine.ui.TextView;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.ResourceManager;
import org.smallbox.faraway.ui.UserInterface.Mode;

public class PanelResource extends BasePanel {
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 	FRAME_HEIGHT = 32;

	private TextView 	_spice;
	private TextView 	_energy;
	private TextView 	_matter;
	private TextView 	_o2;
	
	public PanelResource() {
		super(Mode.NONE, null, Constant.WINDOW_WIDTH - FRAME_WIDTH, 0, FRAME_WIDTH, FRAME_HEIGHT);
	}

	@Override
	protected void onCreate(LayoutFactory factory) {
		_spice = ViewFactory.getInstance().createTextView(10, 10);
		_spice.setCharacterSize(14);
		_spice.setColor(Color.WHITE);
		_spice.setPosition(10, 6);
		addView(_spice);
		
		_energy = ViewFactory.getInstance().createTextView(10, 10);
		_energy.setCharacterSize(14);
		_energy.setColor(Color.WHITE);
		_energy.setPosition(110, 6);
		addView(_energy);

		_matter = ViewFactory.getInstance().createTextView(10, 10);
		_matter.setCharacterSize(14);
		_matter.setColor(Color.WHITE);
		_matter.setPosition(210, 6);
		addView(_matter);

		_o2 = ViewFactory.getInstance().createTextView(10, 10);
		_o2.setCharacterSize(14);
		_o2.setColor(Color.WHITE);
		_o2.setPosition(332, 6);
		addView(_o2);
		
		setAlwaysVisible(true);
	}
	
	@Override
	public void onRefresh(int frame) {
        _spice.setString("Food: " + String.valueOf(ResourceManager.getInstance().getFood().value));
        _o2.setString("O2: " + String.valueOf(ResourceManager.getInstance().getO2().value));
        _energy.setString("PW: " + String.valueOf(ResourceManager.getInstance().getPower().value));
        _matter.setString("M: " + String.valueOf(ResourceManager.getInstance().getMatter().value));
	}
}
