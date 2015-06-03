package org.smallbox.faraway.ui.panel;

import org.smallbox.faraway.Color;
import org.smallbox.faraway.GameEventListener;
import org.smallbox.faraway.Strings;
import org.smallbox.faraway.engine.ui.*;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.model.room.Room.Type;
import org.smallbox.faraway.model.room.RoomOptions;
import org.smallbox.faraway.model.room.RoomOptions.RoomOption;
import org.smallbox.faraway.ui.UserInteraction.Action;
import org.smallbox.faraway.ui.UserInterface.Mode;

import java.util.*;

public class PanelRoom extends BaseRightPanel {

	private class PanelEntry {
		public GameEventListener.Key				shortcut;
		public String			label;
		public Type				roomType;
		public OnClickListener 	onClickListener;
		public View 			view;

		public PanelEntry(final String label, final Type roomType, final GameEventListener.Key shortcut) {
			this.shortcut = shortcut;
			this.label = label;
			this.roomType = roomType;
			this.onClickListener = new OnClickListener() {
				@Override
				public void onClick(View view) {
					_interaction.set(Action.SET_ROOM, roomType);
					clearButtons();
					view.setBackgroundColor(new Color(29, 85, 96));
					view.setBorderColor(new Color(161, 255, 255));
				}
			};
		}

		public void click() {
			onClickListener.onClick(this.view);			
		}
	}

	private static final int 		FRAME_WIDTH = Constant.PANEL_WIDTH;
	private static final int 		FRAME_HEIGHT = Constant.WINDOW_HEIGHT;
	private static final int 		MAX_OCCUPANTS = 10;

	private Map<Integer, TextView> 		_icons;
	private Room 						_room;
	private FrameLayout 				_layoutRoom;
	private TextView 					_lbRoomName;
	private TextView[] 					_lbRoomOccupants;
	private FrameLayout 				_layoutButtons;
	private TextView[] 					_lbRoomOccupantsOld;
	private List<View> 					_buttons;
	private FrameLayout 				_layoutRoomOption;

	private PanelEntry[] _entries = {
			new PanelEntry("Remove", Room.Type.NONE, GameEventListener.Key.R),
			new PanelEntry("Quarter", Room.Type.QUARTER, GameEventListener.Key.Q),
			new PanelEntry("Sickbay", Room.Type.SICKBAY, GameEventListener.Key.I),
			new PanelEntry("Enginering", Room.Type.ENGINEERING, GameEventListener.Key.E),
			new PanelEntry("Pub", Room.Type.METTING, GameEventListener.Key.P),
			new PanelEntry("Holodeck", Room.Type.HOLODECK, GameEventListener.Key.H),
			new PanelEntry("Garden", Room.Type.GARDEN, GameEventListener.Key.G),
			new PanelEntry("Storage", Room.Type.STORAGE, GameEventListener.Key.S),
	};

	public PanelRoom(Mode mode, GameEventListener.Key shortcut) {
		super(mode, shortcut);
	}

	@Override
	protected void onCreate(ViewFactory factory) {
		_buttons = new ArrayList<>();
		_icons = new HashMap<>();
		_layoutButtons = factory.createFrameLayout(200, 400);
		_layoutButtons.setVisible(true);
		addView(_layoutButtons);

		int i = 0;
		for (PanelEntry entry: _entries) {
			createIconEntry(entry, i++);
		}

		createRoomInfo(0, 250);
	}

	protected boolean onKey(GameEventListener.Key key) {
		for (PanelEntry entry: _entries) {
			if (key.equals(entry.shortcut)) {
				entry.click();
				return true;
			}
		}
		return false;
	}

	private void createRoomInfo(int x, int y) {
		_layoutRoom = SpriteManager.getInstance().createFrameLayout();
		_layoutRoom.setPosition(Constant.UI_PADDING_H + x, Constant.UI_PADDING_H + y);
		_layoutRoom.setVisible(false);
		addView(_layoutRoom);

		_layoutRoomOption = SpriteManager.getInstance().createFrameLayout();
		_layoutRoomOption.setPosition(0, 200);
		_layoutRoom.addView(_layoutRoomOption);

		View border = ViewFactory.getInstance().createColorView(FRAME_WIDTH - 40, 4);
		border.setBackgroundColor(Colors.BORDER);
		_layoutRoom.addView(border);

		_lbRoomName = SpriteManager.getInstance().createTextView();
		_lbRoomName.setCharacterSize(FONT_SIZE_TITLE);
		_lbRoomName.setPosition(0, 12);
		_layoutRoom.addView(_lbRoomName);

		_lbRoomOccupants = new TextView[MAX_OCCUPANTS];
		_lbRoomOccupantsOld = new TextView[MAX_OCCUPANTS];
		for (int i = 0; i < MAX_OCCUPANTS; i++) {
			_lbRoomOccupants[i] = SpriteManager.getInstance().createTextView();
			_lbRoomOccupants[i].setCharacterSize(14);
			_lbRoomOccupants[i].setPosition(0, 48 + 22 * i);
			_layoutRoom.addView(_lbRoomOccupants[i]);

			_lbRoomOccupantsOld[i] = SpriteManager.getInstance().createTextView();
			_lbRoomOccupantsOld[i].setCharacterSize(14);
			_lbRoomOccupantsOld[i].setPosition(FRAME_WIDTH - 100, 48 + 22 * i);
			_layoutRoom.addView(_lbRoomOccupantsOld[i]);
		}
	}

	private void createRoomInfoOption(Room room) {
		_layoutRoomOption.clearAllViews();
		int i = 0;
		RoomOptions options = room.getOptions();
		if (options != null) {
			if (options.title != null) {
				TextView lbOptionTitle = SpriteManager.getInstance().createTextView();
				lbOptionTitle.setPosition(0, 0);
				lbOptionTitle.setCharacterSize(FONT_SIZE_TITLE);
				lbOptionTitle.setString(options.title);
				_layoutRoomOption.addView(lbOptionTitle);
			}
			
			for (RoomOption option: options.options) {
				if (option.icon != null) {
					ImageView iconOption = ViewFactory.getInstance().createImageView();
					iconOption.setPosition(0, 42 + i * 32);
					iconOption.setImage(option.icon);
					iconOption.setOnClickListener(option.onClickListener);
					_layoutRoomOption.addView(iconOption);
				}

				TextView lbOption = ViewFactory.getInstance().createTextView();
				lbOption.setPosition(option.icon != null ? 40 : 0, 42 + i * 32);
				lbOption.setCharacterSize(FONT_SIZE);
				lbOption.setString(option.label);
				lbOption.setOnClickListener(option.onClickListener);
				_layoutRoomOption.addView(lbOption);

				i++;
			}
		}
	}

	@Override
	public void onRefresh(int frame) {
		if (_interaction.getSelectedRoomType() == null) {
			clearButtons();
		}

		// New room is selected
		Room selectedRoom = _ui.getSelectedRoom();
		if (selectedRoom != _room) {
			setRoom(selectedRoom);
		}

		// Room is selected
		if (_room != null) {
			displayRoom(selectedRoom);
		} else {
			_layoutRoom.setVisible(false);
		}
	}

	private void setRoom(Room room) {
		_room = room;
		if (room != null) {
			createRoomInfoOption(room);
		}
	}

	void	createIconEntry(final PanelEntry entry, int index) {
		TextView icon = _icons.get(index);
		if (icon == null) {
			icon = ViewFactory.getInstance().createTextView(62, 80);
			icon.setString(entry.label);
			icon.setCharacterSize(FONT_SIZE);
			icon.setIcon(SpriteManager.getInstance().getFloor(null, index, 0));
			icon.setPosition(20 + (index % 4) * 80, 60 + (int)(index / 4) * 100);
			entry.view = icon;
			icon.setOnClickListener(entry.onClickListener);
			_buttons.add(icon);
			_layoutButtons.addView(icon);

			_icons.put(index, icon);
		}
	}

	private void displayRoom(Room room) {
		_layoutRoom.setVisible(true);
		_lbRoomName.setString(room.getName() + " #" + room.getId());

		int i = 0;
		Set<CharacterModel> occupants = room.getOccupants();
		for (CharacterModel character: occupants) {
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
	}

	protected void clearButtons() {
		for (View button: _buttons) {
			button.setBackgroundColor(new Color(29, 85, 96, 100));
			button.setBorderColor(null);
		}
	}

}
