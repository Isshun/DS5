package alone.in.deepspace.ui.panel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;

import alone.in.deepspace.Strings;
import alone.in.deepspace.engine.ui.ButtonView;
import alone.in.deepspace.engine.ui.FrameLayout;
import alone.in.deepspace.engine.ui.LinkView;
import alone.in.deepspace.engine.ui.OnClickListener;
import alone.in.deepspace.engine.ui.TextView;
import alone.in.deepspace.engine.ui.View;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.manager.SpriteManager;
import alone.in.deepspace.model.character.Character;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.room.GardenRoom;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.model.room.Room.Type;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.util.Constant;

public class PanelRoom extends BasePanel {
	private static final Color 		COLOR_YELLOW = new Color(236, 201, 37);
	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final String[]	TEXTS = {"Remove", "Quarter", "Sickbay", "Engineering", "Pub", "Holodeck", "Store", "Garden"};
	private static final int 		MAX_OCCUPANTS = 10;
	
	private Type 					_selected;
	private Map<Integer, ButtonView> 	_icons;
	private Room _room;
	private FrameLayout _layoutRoomInfo;
	private TextView _lbRoomName;
	private TextView[] _lbRoomOccupants;
	private FrameLayout _layoutButtons;
	private TextView[] _lbRoomOccupantsOld;

	public PanelRoom(Mode mode) {
		super(mode, new Vector2f(Constant.WINDOW_WIDTH - FRAME_WIDTH, 32), new Vector2f(FRAME_WIDTH, FRAME_HEIGHT - 32), true);
		_icons = new HashMap<Integer, ButtonView>();
		_layoutButtons = new FrameLayout(new Vector2f(200, 400));
		_layoutButtons.setVisible(true);
		addView(_layoutButtons);

		createRoomInfo(0, 0);
	}

	private void createRoomInfo(int x, int y) {
		_layoutRoomInfo = new FrameLayout(new Vector2f(FRAME_WIDTH - Constant.UI_PADDING_H * 2, 200));
		_layoutRoomInfo.setPosition(Constant.UI_PADDING_H + x, Constant.UI_PADDING_H + y);
		_layoutRoomInfo.setVisible(false);
		addView(_layoutRoomInfo);
		
		_lbRoomName = new TextView();
		_lbRoomName.setCharacterSize(32);
		_lbRoomName.setPosition(8, 0);
		_layoutRoomInfo.addView(_lbRoomName);

		_lbRoomOccupants = new TextView[MAX_OCCUPANTS];
		_lbRoomOccupantsOld = new TextView[MAX_OCCUPANTS];
		for (int i = 0; i < MAX_OCCUPANTS; i++) {
			_lbRoomOccupants[i] = new TextView();
			_lbRoomOccupants[i].setCharacterSize(14);
			_lbRoomOccupants[i].setPosition(8, 42 + 22 * i);
			_layoutRoomInfo.addView(_lbRoomOccupants[i]);

			_lbRoomOccupantsOld[i] = new TextView();
			_lbRoomOccupantsOld[i].setCharacterSize(14);
			_lbRoomOccupantsOld[i].setPosition(FRAME_WIDTH - 100, 42 + 22 * i);
			_layoutRoomInfo.addView(_lbRoomOccupantsOld[i]);
		}

		for (int i = 0; i < 10; i++) {
			final ItemInfo info = ServiceManager.getData().getItemInfo("base.seaweed" + i);
			LinkView lbOption = new LinkView();
			lbOption.setPosition(20, 200 + i * 20);
			lbOption.setCharacterSize(FONT_SIZE);
			lbOption.setString(info.name);
			lbOption.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					((GardenRoom)_room).setCulture(info);
				}
			});
			_layoutRoomInfo.addView(lbOption);
		}
	}

	@Override
	public void onDraw(RenderWindow app, RenderStates render) {
		for (int i = 0; i < TEXTS.length; i++) {
			drawIcon(0, i);
		}
		displayRoom(_room);
	}

	public Room.Type getSelectedRoom() {
		return _selected;
	}
	
	void	drawIcon(int offset, final int index) {
		ButtonView icon = _icons.get(index);
		if (icon == null) {
			icon = new ButtonView(new Vector2f(62, 80));
			icon.setString(TEXTS[index]);
			icon.setCharacterSize(FONT_SIZE);
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
					case 4: _selected = Room.Type.METTING; break;
					case 5: _selected = Room.Type.HOLODECK; break;
					case 6: _selected = Room.Type.STORAGE; break;
					case 7: _selected = Room.Type.GARDEN; break;
					}
					((ButtonView) view).setBackgroundColor(Color.RED);
				}
			});
			_layoutButtons.addView(icon);

			_icons.put(index, icon);
		}
	}

	public void select(Room.Type type) {
		_selected = type;
	}

	public void setRoom(Room room) {
		if (_room != room) {
			_room = room;
			displayRoom(room);
		}
	}

	private void displayRoom(Room room) {
		if (room == null) {
			_layoutRoomInfo.setVisible(false);
			_layoutButtons.setVisible(true);
			return;
		}
		
		_layoutRoomInfo.setVisible(true);
		_layoutButtons.setVisible(false);
		_lbRoomName.setString(room.getName());
		
		int i = 0;
		Set<Character> occupants = room.getOccupants();
		for (Character character: occupants) {
			if (i < MAX_OCCUPANTS) {
				_lbRoomOccupants[i].setString(character.getName());
				_lbRoomOccupants[i].setColor(character.getColor());
				_lbRoomOccupantsOld[i].setString((int)character.getOld() + "yo.");
				i++;
			}
		}
		for (;i < MAX_OCCUPANTS; i++) {
			_lbRoomOccupants[i].setString(i == 0 ? Strings.LB_NOBODY : "");
			_lbRoomOccupantsOld[i].setString("");
		}
		_layoutRoomInfo.setBackgroundColor(new Color(50 + room.getColor().r, 50 + room.getColor().g, 50 + room.getColor().b, 100));
	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		
	}

}
