package alone.in.deepspace.ui.panel;

import org.jsfml.graphics.Color;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.manager.ResourceManager;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelResource extends BasePanel {
	private static final int 	FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 	FRAME_HEIGHT = 32;

	private TextView 	_spice;
	private TextView 	_energy;
	private TextView 	_matter;
	private TextView 	_o2;
	
	public PanelResource() {
		super(Mode.NONE, null, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT), false);
	}

	@Override
	protected void onCreate() {
		_spice = new TextView(new Vector2f(10, 10));
		_spice.setCharacterSize(14);
		_spice.setColor(Color.WHITE);
		_spice.setPosition(new Vector2f(10, 6));
		addView(_spice);
		
		_energy = new TextView(new Vector2f(10, 10));
		_energy.setCharacterSize(14);
		_energy.setColor(Color.WHITE);
		_energy.setPosition(new Vector2f(110, 6));
		addView(_energy);

		_matter = new TextView(new Vector2f(10, 10));
		_matter.setCharacterSize(14);
		_matter.setColor(Color.WHITE);
		_matter.setPosition(new Vector2f(210, 6));
		addView(_matter);

		_o2 = new TextView(new Vector2f(10, 10));
		_o2.setCharacterSize(14);
		_o2.setColor(Color.WHITE);
		_o2.setPosition(new Vector2f(332, 6));
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
