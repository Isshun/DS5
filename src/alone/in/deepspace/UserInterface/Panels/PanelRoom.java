package alone.in.deepspace.UserInterface.Panels;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Engine.ui.OnClickListener;
import alone.in.deepspace.Engine.ui.ButtonView;
import alone.in.deepspace.Engine.ui.View;
import alone.in.deepspace.Managers.SpriteManager;
import alone.in.deepspace.Models.Room;
import alone.in.deepspace.Models.Room.Type;
import alone.in.deepspace.UserInterface.UserSubInterface;
import alone.in.deepspace.Utils.Constant;

public class PanelRoom extends UserSubInterface {
	private static final Color COLOR_YELLOW = new Color(236, 201, 37);
	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final String[]	TEXTS = {"Remove", "Quarter", "Sickbay", "Engineering", "Pub", "Holodeck", "Store", "Garden"};
	
	private Type 					_selected;
	private Map<Integer, ButtonView> 	_icons;

	public PanelRoom(RenderWindow app) throws IOException {
		super(app, 0, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 0), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT));
		_icons = new HashMap<Integer, ButtonView>();
	}

	@Override
	public void onRefresh(RenderWindow app) {
		try {
			for (int i = 0; i < TEXTS.length; i++) {
				drawIcon(0, i);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Room.Type getSelectedRoom() {
		return _selected;
	}
	
	void	drawIcon(int offset, final int index) throws IOException {
		ButtonView icon = _icons.get(index);
		if (icon == null) {
			icon = new ButtonView(new Vector2f(62, 80), TEXTS[index]);
			icon.setIcon(SpriteManager.getInstance().getFloor(null, index, 0));
			icon.setPosition(20 + (index % 4) * 80, 60 + offset + (int)(index / 4) * 100);
			icon.setBackgroundColor(index == 0 ? Color.RED : new Color(0, 150, 180, 255));
			icon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					for (ButtonView icon: _icons.values()) {
						icon.setBackgroundColor(COLOR_YELLOW);
					}
					switch (index) {
					case 0: _selected = Room.Type.NONE; break;
					case 1: _selected = Room.Type.QUARTER; break;
					case 2: _selected = Room.Type.SICKBAY; break;
					case 3: _selected = Room.Type.ENGINEERING; break;
					case 4: _selected = Room.Type.PUB; break;
					case 5: _selected = Room.Type.HOLODECK; break;
					case 6: _selected = Room.Type.STORAGE; break;
					case 7: _selected = Room.Type.GARDEN; break;
					}
					((ButtonView) view).setBackgroundColor(Color.RED);
				}
			});
			addView(icon);

			_icons.put(index, icon);
		}
	}

	public void setSelected(Room.Type type) {
		_selected = type;
	}

}
