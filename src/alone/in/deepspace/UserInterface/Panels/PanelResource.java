package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Managers.ResourceManager;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.UserInterface.Utils.UIText;
import alone.in.deepspace.Utils.Constant;

public class PanelResource extends UserSubInterface {
	private static final int FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int FRAME_HEIGHT = 32;
	private UIText _spice;
	private UIText _energy;
	private UIText _matter;
	private UIText _o2;
	
	public PanelResource(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		
		setBackgroundColor(new Color(200, 200, 140, 150));
		
		_spice = new UIText(new Vector2f(10, 10));
		_spice.setCharacterSize(14);
		_spice.setColor(Color.WHITE);
		_spice.setPosition(new Vector2f(10, 6));
		addView(_spice);
		
		_energy = new UIText(new Vector2f(10, 10));
		_energy.setCharacterSize(14);
		_energy.setColor(Color.WHITE);
		_energy.setPosition(new Vector2f(110, 6));
		addView(_energy);

		_matter = new UIText(new Vector2f(10, 10));
		_matter.setCharacterSize(14);
		_matter.setColor(Color.WHITE);
		_matter.setPosition(new Vector2f(210, 6));
		addView(_matter);

		_o2 = new UIText(new Vector2f(10, 10));
		_o2.setCharacterSize(14);
		_o2.setColor(Color.WHITE);
		_o2.setPosition(new Vector2f(332, 6));
		addView(_o2);
	}
	
	@Override
	public void onRefresh(RenderWindow app) {
		
        _spice.setString("SP: " + String.valueOf(ResourceManager.getInstance().getSpice()));
        _o2.setString("O2: " + String.valueOf(ResourceManager.getInstance().getO2()));
        _energy.setString("PW: " + String.valueOf(ResourceManager.getInstance().getPower()));
        _matter.setString("M: " + String.valueOf(ResourceManager.getInstance().getMatter()));
	}
}
