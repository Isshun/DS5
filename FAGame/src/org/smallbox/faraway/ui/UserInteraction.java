package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.AreaManager;
import org.smallbox.faraway.game.manager.JobManager;
import org.smallbox.faraway.game.model.area.AreaType;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.item.StructureModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobDump;
import org.smallbox.faraway.game.model.job.JobHaul;
import org.smallbox.faraway.game.model.room.RoomModel.RoomType;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.cursor.DumpCursor;
import org.smallbox.faraway.ui.cursor.GatherCursor;
import org.smallbox.faraway.ui.cursor.MineCursor;
import org.smallbox.faraway.ui.cursor.PickCursor;
import org.smallbox.faraway.ui.panel.right.PanelPlan.Planning;
import org.smallbox.faraway.util.Log;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UserInteraction {

	public boolean onKeyLeft(int x, int y, int fromX, int fromY, int toX, int toY) {
		// Set plan
		if (_action == Action.SET_PLAN) {
			plan(fromX, fromY, toX, toY);
			return true;
		}

		// Set area
		if (_action == Action.SET_AREA) {
			((AreaManager)Game.getInstance().getManager(AreaManager.class)).createArea(_selectedAreaType, fromX, fromY, toX, toY);
			return true;
		}

		// Set area
		if (_action == Action.REMOVE_AREA) {
			((AreaManager)Game.getInstance().getManager(AreaManager.class)).removeArea(_selectedAreaType, fromX, fromY, toX, toY);
			return true;
		}

		// Set room
		if (_action == Action.SET_ROOM) {
			roomType(x, y, fromX, fromY, toX, toY);
			return true;
		}

		// Remove item
		if (_action == Action.REMOVE_ITEM) {
			removeItem(fromX, fromY, toX, toY);
			return true;
		}

		// Remove structure
		if (_action == Action.REMOVE_STRUCTURE) {
			removeStructure(fromX, fromY, toX, toY);
			return true;
		}

		// Build item
		if (_action == Action.BUILD_ITEM) {
			planBuild(fromX, fromY, toX, toY);
			return true;
		}

		// Build item free
		if (_action == Action.PUT_ITEM_FREE) {
			planPutForFree(fromX, fromY, toX, toY);
			return true;
		}

		return false;
	}

	public enum Action {
		NONE, REMOVE_ITEM, REMOVE_STRUCTURE, BUILD_ITEM, SET_ROOM, SET_AREA, PUT_ITEM_FREE, REMOVE_AREA, SET_PLAN
	}

	Action					            _action;
	int						            _startPressX;
	int						            _startPressY;
	int						            _mouseMoveX;
	int						            _mouseMoveY;
	GameEventListener.MouseButton       _button;
	private Planning 					_selectedPlan;
	private RoomType 					_selectedRoomType;
	private ItemInfo 					_selectedItemInfo;
	private AreaType _selectedAreaType;
	private UserInterface				_ui;

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
				ResourceModel res = Game.getWorldManager().getResource(x, y);
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
						StructureModel structure = Game.getWorldManager().getStructure(x, y);
						if (structure == null || !structure.isDoor()) {
							JobManager.getInstance().build(GameData.getData().getItemInfo("base.wall"), x, y);
						}
						// item = Game.getWorldManager().putObject(x, y, BaseItem.STRUCTURE_WALL);
					} else {
						Log.warning("2");
						// TODO
						JobManager.getInstance().build(GameData.getData().getItemInfo("base.floor"), x, y);
						// item = Game.getWorldManager().putObject(x, y, BaseItem.STRUCTURE_FLOOR);
					}
				} else {
					// item = Game.getWorldManager().putObject(x, y, _menu.getBuildItemType());
					if (_selectedItemInfo != null) {
						Log.warning("3 " + _selectedItemInfo.name);
						// TODO
						JobManager.getInstance().build(_selectedItemInfo, x, y);
						// item = Game.getWorldManager().putObject(x, y, type);
					}
				}

				// if (item != NULL) {
				// }
			}
		}
	}

	public void	planPutForFree(int startX, int startY, int toX, int toY) {
		if (_selectedItemInfo == null) {
			return;
		}

		for (int x = toX; x >= startX; x--) {
			for (int y = toY; y >= startY; y--) {
				if (_selectedItemInfo != null) {
					Log.warning("3 " + _selectedItemInfo.name);
					Game.getWorldManager().putObject(_selectedItemInfo, x, y, 0, 10);
				}
			}
		}
	}

	public void removeItem(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Game.getWorldManager().takeItem(x, y);
			}
		}
	}

	public void removeStructure(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				Game.getWorldManager().removeStructure(x, y);
			}
		}
	}

	public void planGather(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				BaseJobModel job = JobManager.getInstance().createGatherJob(x, y);
				if (job != null) {
					JobManager.getInstance().addJob(job);
				}
			}
		}
	}

	public void planMining(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				BaseJobModel job = JobManager.getInstance().createMiningJob(x, y);
				if (job != null) {
					JobManager.getInstance().addJob(job);
				}
			}
		}
	}

	public void planPick(int startX, int startY, int toX, int toY) {
//		for (int x = startX; x <= toX; x++) {
//			for (int y = startY; y <= toY; y++) {
//				ItemModel item = Game.getWorldManager().getItem(x, y);
//				if (item != null) {
//					JobModel job = JobTake.onCreate(item);
//					if (job != null) {
//						JobManager.getInstance().addJob(job);
//					}
//				}
//			}
//		}
		throw new NotImplementedException();
	}

	public void planDump(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if (Game.getWorldManager().getItem(x, y) != null) {
					JobManager.getInstance().addJob(JobDump.create(Game.getWorldManager().getItem(x, y)));
				}
				if (Game.getWorldManager().getStructure(x, y) != null) {
					JobManager.getInstance().addJob(JobDump.create(Game.getWorldManager().getStructure(x, y)));
				}
			}
		}
	}

	public void planHaul(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if (Game.getWorldManager().getConsumable(x, y) != null) {
					JobManager.getInstance().addJob(JobHaul.create(Game.getWorldManager().getConsumable(x, y)));
				}
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
		case PICK: planPick(startX, startY, toX, toY); break;
		case HAUL: planHaul(startX, startY, toX, toY); break;
		default: break;
		}
	}

	public void roomType(int clickX, int clickY, int fromX, int fromY, int toX, int toY) {
		if (_selectedRoomType == null) {
			return;
		}

//		if (_selectedRoomType == Room.Type.NONE) {
//			Game.getRoomManager().removeRoom(fromX, fromY, toX, toY);
//		} else {
//			Game.getRoomManager().putRoom(clickX, clickY, fromX, fromY, toX, toY, _selectedRoomType, null);
//		}

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

	public RoomType getSelectedRoomType() {
		return _selectedRoomType;
	}

	public void clean() {
		_action = Action.NONE;
		_selectedPlan = null;
		_selectedRoomType = null;
		_selectedItemInfo = null;
	}

	public void set(Action action, Planning plan) {
		_action = action;
		_selectedPlan = plan;
		switch (plan) {
			case GATHER:
				UserInterface.getInstance().setCursor(new GatherCursor());
				break;
			case MINING:
				UserInterface.getInstance().setCursor(new MineCursor());
				break;
			case DUMP:
				UserInterface.getInstance().setCursor(new DumpCursor());
				break;
			case PICK:
				UserInterface.getInstance().setCursor(new PickCursor());
				break;
			case HAUL:
				UserInterface.getInstance().setCursor(new PickCursor());
				break;
		}
	}

	public void set(Action action, ItemInfo info) {
		_action = action;
		_selectedItemInfo = info;
	}

    public void set(Action action, RoomType roomType) {
        _action = action;
        _selectedRoomType = roomType;
    }

    public void set(Action action, AreaType areaType) {
        _action = action;
        _selectedAreaType = areaType;
    }
}
