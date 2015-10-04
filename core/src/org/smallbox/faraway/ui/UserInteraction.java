package org.smallbox.faraway.ui;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.helper.JobHelper;
import org.smallbox.faraway.game.helper.WorldHelper;
import org.smallbox.faraway.game.model.area.AreaType;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ResourceModel;
import org.smallbox.faraway.game.model.job.BaseJobModel;
import org.smallbox.faraway.game.model.job.JobDump;
import org.smallbox.faraway.game.model.job.JobHaul;
import org.smallbox.faraway.game.module.ModuleHelper;
import org.smallbox.faraway.ui.UserInterface.Mode;
import org.smallbox.faraway.ui.cursor.*;
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
            Game.getInstance().notify(gameObserver -> gameObserver.onAddArea(_selectedAreaType, fromX, fromY, toX, toY));
			return true;
		}

		// Set area
		if (_action == Action.REMOVE_AREA) {
            Game.getInstance().notify(gameObserver -> gameObserver.onRemoveArea(_selectedAreaType, fromX, fromY, toX, toY));
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
		NONE, REMOVE_ITEM, REMOVE_STRUCTURE, BUILD_ITEM, SET_AREA, PUT_ITEM_FREE, REMOVE_AREA, SET_PLAN
	}

	Action					            _action;
	int						            _startPressX;
	int						            _startPressY;
	int						            _mouseMoveX;
	int						            _mouseMoveY;
	GameEventListener.MouseButton       _button;
	private String 						_selectedPlan;
	private ItemInfo 					_selectedItemInfo;
	private AreaType 					_selectedAreaType;
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

		ItemInfo itemInfo = _selectedItemInfo;
		for (int x = toX; x >= startX; x--) {
			for (int y = toY; y >= startY; y--) {

				// Check if resource is present on area
				ResourceModel res = WorldHelper.getResource(x, y);
				if (res != null) {
					if (res.canBeMined()) {
						JobHelper.addMineJob(x, y);
					} else if (res.canBeHarvested()) {
						JobHelper.addGatherJob(x, y, true);
					}
				}

				JobHelper.addBuildJob(itemInfo, x, y);

//				if (_selectedItemInfo.name.equals("base.room")) {
//					if (x == startX || x == toX || y == startY || y == toY) {
//						StructureModel structure = ModuleHelper.getWorldModule().getStructure(x, y);
//						if (structure == null || !structure.isDoor()) {
//							JobHelper.addBuildJob(GameData.getData().getItemInfo("base.wall"), x, y);
//						}
//					} else {
//						JobHelper.addBuildJob(GameData.getData().getItemInfo("base.floor"), x, y);
//					}
//				}
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
					ModuleHelper.getWorldModule().putObject(_selectedItemInfo, x, y, 0, 10);
				}
			}
		}
	}

	public void removeItem(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				ModuleHelper.getWorldModule().takeItem(x, y);
			}
		}
	}

	public void removeStructure(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				ModuleHelper.getWorldModule().removeStructure(x, y);
			}
		}
	}

	public void planGather(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				BaseJobModel job = JobHelper.createGatherJob(x, y);
				if (job != null) {
					ModuleHelper.getJobModule().addJob(job);
				}
			}
		}
	}

	public void planMining(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				BaseJobModel job = JobHelper.createMiningJob(x, y);
				if (job != null) {
					ModuleHelper.getJobModule().addJob(job);
				}
			}
		}
	}

	public void planPick(int startX, int startY, int toX, int toY) {
//		for (int x = startX; x <= toX; x++) {
//			for (int y = startY; y <= toY; y++) {
//				ItemModel item = ModuleHelper.getWorldModule().getItem(x, y);
//				if (item != null) {
//					JobModel job = JobTake.onCreate(item);
//					if (job != null) {
//						ModuleHelper.getJobModule().addJob(job);
//					}
//				}
//			}
//		}
		throw new NotImplementedException();
	}

	public void planDump(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if (WorldHelper.getItem(x, y) != null) {
					ModuleHelper.getJobModule().addJob(JobDump.create(WorldHelper.getItem(x, y)));
				}
				if (WorldHelper.getStructure(x, y) != null) {
					ModuleHelper.getJobModule().addJob(JobDump.create(WorldHelper.getStructure(x, y)));
				}
			}
		}
	}

	public void planHaul(int startX, int startY, int toX, int toY) {
		for (int x = startX; x <= toX; x++) {
			for (int y = startY; y <= toY; y++) {
				if (WorldHelper.getConsumable(x, y) != null && WorldHelper.getConsumable(x, y).getHaul() == null) {
					ModuleHelper.getJobModule().addJob(JobHaul.create(WorldHelper.getConsumable(x, y)));
				}
			}
		}
	}

	public void plan(int startX, int startY, int toX, int toY) {
		if (_selectedPlan == null) {
			return;
		}
		
		switch (_selectedPlan) {
		case "dump": planDump(startX, startY, toX, toY); break;
		case "gather": planGather(startX, startY, toX, toY); break;
		case "mining": planMining(startX, startY, toX, toY); break;
		case "pick": planPick(startX, startY, toX, toY); break;
		case "haul": planHaul(startX, startY, toX, toY); break;
		default: break;
		}
	}

	public boolean isAction(Action action) {
		return _action.equals(action);
	}

	public void select(ItemInfo info, Mode mode) {
		if (mode == Mode.BUILD) {
			clean();
//			_ui.setMode(Mode.BUILD);
			_action = Action.BUILD_ITEM;
			_selectedItemInfo = info;
		}
	}

	public void clean() {
		_action = Action.NONE;
		_selectedPlan = null;
		_selectedItemInfo = null;
	}

	public void set(Action action, String plan) {
		_action = action;
		_selectedPlan = plan;
		switch (plan) {
			case "gather":
				UserInterface.getInstance().setCursor(new GatherCursor());
				break;
			case "mining":
				UserInterface.getInstance().setCursor(new MineCursor());
				break;
			case "dump":
				UserInterface.getInstance().setCursor(new DumpCursor());
				break;
			case "pick":
				UserInterface.getInstance().setCursor(new PickCursor());
				break;
			case "haul":
				UserInterface.getInstance().setCursor(new PickCursor());
				break;
		}
	}

	public void set(Action action, ItemInfo info) {
		_action = action;
		_selectedItemInfo = info;
	}

    public void set(Action action, AreaType areaType) {
        _action = action;
        _selectedAreaType = areaType;

		switch (action) {
			case NONE:
				break;
			case REMOVE_ITEM:
				break;
			case REMOVE_STRUCTURE:
				break;
			case BUILD_ITEM:
				break;
			case SET_AREA:
				UserInterface.getInstance().setCursor(new AreaCursor());
				break;
			case PUT_ITEM_FREE:
				break;
			case REMOVE_AREA:
				UserInterface.getInstance().setCursor(new AreaCursor());
				break;
			case SET_PLAN:
				break;
		}
    }
}
