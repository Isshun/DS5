package alone.in.deepspace.ui;

import org.jsfml.graphics.Texture;
import org.jsfml.window.Mouse;

import alone.in.deepspace.Game;
import alone.in.deepspace.manager.JobManager;
import alone.in.deepspace.manager.ServiceManager;
import alone.in.deepspace.model.item.ItemInfo;
import alone.in.deepspace.model.item.StructureItem;
import alone.in.deepspace.model.item.WorldResource;
import alone.in.deepspace.model.job.Job;
import alone.in.deepspace.model.room.Room;
import alone.in.deepspace.model.room.Room.Type;
import alone.in.deepspace.ui.UserInterface.Mode;
import alone.in.deepspace.ui.panel.PanelPlan.Planning;
import alone.in.deepspace.util.Log;

public class UserInteraction {
	public enum Action {
		NONE, REMOVE_ITEM, REMOVE_STRUCTURE, BUILD_ITEM, SET_ROOM, SET_PLAN
	}

	Texture					_cursorTexture;
	Action					_action;
	int						_startPressX;
	int						_startPressY;
	int						_mouseMoveX;
	int						_mouseMoveY;
	Mouse.Button			_button;
	
	private Planning 					_selectedPlan;
	private Type 						_selectedRoomType;
	private Room 						_selectedRoom;
	private UserInterface				_ui;
	private ItemInfo _selectedItemInfo;

	UserInteraction(UserInterface ui) {
		_startPressX = 0;
		_startPressY = 0;
		_mouseMoveX = 0;
		_mouseMoveY = 0;
		_button = null;
		_action = Action.NONE;
		_ui = ui;
	}

	public Action  getAction() { return _action; }

	public void	planBuild(int startX, int startY, int toX, int toY) {
		if (_selectedItemInfo == null) {
			return;
		}
		
		for (int x = toX; x >= startX; x--) {
			for (int y = toY; y >= startY; y--) {

				// Check if resource is present on area
				WorldResource res = ServiceManager.getWorldMap().getRessource(x, y);
				if (res != null) {
					if (res.canBeMined()) {
						JobManager.getInstance().addMineJob(x, y);
					} else if (res.canBeHarvested()) {
						JobManager.getInstance().addGatherJob(x, y);
					}
				}
				
				//TODO
				// Structure
				if (_selectedItemInfo.name.equals("base.room")) {
					if (x == startX || x == toX || y == startY || y == toY) {
						Log.warning("1");
						// TODO
						StructureItem structure = ServiceManager.getWorldMap().getStructure(x, y);
						if (structure == null || structure.getName().equals("base.door") == false) {
							JobManager.getInstance().build(Game.getData().getItemInfo("base.wall"), x, y);
						}
						// item = ServiceManager.getWorldMap().putItem(x, y, BaseItem.STRUCTURE_WALL);
					} else {
						Log.warning("2");
						// TODO
						JobManager.getInstance().build(Game.getData().getItemInfo("base.floor"), x, y);
						// item = ServiceManager.getWorldMap().putItem(x, y, BaseItem.STRUCTURE_FLOOR);
					}
				} else {
					// item = ServiceManager.getWorldMap().putItem(x, y, _menu.getBuildItemType());
					if (_selectedItemInfo != null) {
						Log.warning("3 " + _selectedItemInfo.name);
						// TODO
						JobManager.getInstance().build(_selectedItemInfo, x, y);
						// item = ServiceManager.getWorldMap().putItem(x, y, type);
					}
				}

				// if (item != NULL) {
				// }
			}
		}
	}

	public void removeItem(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				ServiceManager.getWorldMap().removeItem(x, y);
			}
		}
	}

	public void removeStructure(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				ServiceManager.getWorldMap().removeStructure(x, y);
			}
		}
	}

	public void planGather(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Job job = JobManager.getInstance().createGatherJob(x, y);
				JobManager.getInstance().addJob(job);
			}
		}
	}

	public void planMining(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Job job = JobManager.getInstance().createMiningJob(x, y);
				JobManager.getInstance().addJob(job);
			}
		}
	}

	public void planDump(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Job job = JobManager.getInstance().createDumpJob(x, y);
				JobManager.getInstance().addJob(job);
			}
		}
	}

	public void plan(int startX, int startY, int toX, int toY) {
		if (_selectedPlan == null) {
			return;
		}
		
		switch (_selectedPlan) {
		case DUMP: planDump(startX, startY, toX, toY); break;
		case GATHER: planGather(startX, startY, toX, toY); break;
		case MINING: planMining(startX, startY, toX, toY); break;
		default: break;
		}
	}

	public void roomType(int clickX, int clickY, int fromX, int fromY, int toX, int toY) {
		if (_selectedRoomType == null) {
			return;
		}
		
		if (_selectedRoomType == Room.Type.NONE) {
			Game.getRoomManager().removeRoom(fromX, fromY, toX, toY, _selectedRoomType);
		} else {
			Game.getRoomManager().putRoom(clickX, clickY, fromX, fromY, toX, toY, _selectedRoomType, null);
		}
		
	}

	public void set(Action action, Type roomType) {
		_action = action;
		_selectedRoomType = roomType;
	}

	public boolean isAction(Action action) {
		return _action.equals(action);
	}

	public void select(ItemInfo info, Mode mode) {
		if (mode == Mode.BUILD) {
			clean();
			_ui.setMode(Mode.BUILD);
			_action = Action.BUILD_ITEM;
			_selectedItemInfo = info;
		}
	}

	public Type getSelectedRoomType() {
		return _selectedRoomType;
	}

	public void clean() {
		_action = Action.NONE;
		_selectedPlan = null;
		_selectedRoom = null;
		_selectedRoomType = null;
		_selectedItemInfo = null;
	}

	public void set(Action action, Planning plan) {
		_action = action;
		_selectedPlan = plan;
	}

	public void set(Action action, ItemInfo info) {
		_action = action;
		_selectedItemInfo = info;
	}

	public void action(int fromX, int fromY, int toX, int toY) {
		// Remove item
		if (_action == Action.REMOVE_ITEM) {
			removeItem(fromX, fromY, toX, toY);
		}

		// Remove structure
		if (_action == Action.REMOVE_STRUCTURE) {
			removeStructure(fromX, fromY, toX, toY);
		}

		// Build item
		if (_action == Action.BUILD_ITEM) {
			planBuild(fromX, fromY, toX, toY);
		}
	}

	public boolean hasAction() {
		return _action != null && _action != Action.NONE;
	}
}
