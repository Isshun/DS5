package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.GameData;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.job.model.DumpJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Log;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class UserInteraction {

    public boolean onKeyLeft(int x, int y, int fromX, int fromY, int toX, int toY) {
        // Set plan
        if (_action == Action.SET_PLAN) {
            plan(fromX, fromY, toX, toY);
            return true;
        }

        // Set model
        if (_action == Action.SET_AREA) {
            Game.getInstance().notify(gameObserver -> gameObserver.onAddArea(_selectedAreaType, fromX, fromY, toX, toY));
            return true;
        }

        // Set model
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

    Action                              _action;
    int                                 _startPressX;
    int                                 _startPressY;
    int                                 _mouseMoveX;
    int                                 _mouseMoveY;
    GameEventListener.MouseButton       _button;
    private String                      _selectedPlan;
    private ItemInfo                    _selectedItemInfo;
    private AreaType                    _selectedAreaType;

    UserInteraction() {
        _startPressX = 0;
        _startPressY = 0;
        _mouseMoveX = 0;
        _mouseMoveY = 0;
        _button = null;
        _action = Action.NONE;
    }

    public Action  getAction() { return _action; }

    public void    planBuild(int startX, int startY, int toX, int toY) {
        if (_selectedItemInfo == null) {
            return;
        }

        ItemInfo itemInfo = _selectedItemInfo;
        for (int x = toX; x >= startX; x--) {
            for (int y = toY; y >= startY; y--) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);
                if (parcel != null) {

                    // Check if resource is present on parcel
                    if (parcel.getResource() != null) {
                        if (parcel.getResource().canBeMined()) {
                            JobHelper.addMineJob(x, y);
                        } else if (parcel.getResource().canBeHarvested()) {
                            JobHelper.addGatherJob(x, y, true);
                        }
                    }

                    ModuleHelper.getWorldModule().putObject(parcel, itemInfo, 0);
                }
            }
        }
    }

    public void    planPutForFree(int startX, int startY, int toX, int toY) {
        if (_selectedItemInfo == null) {
            return;
        }

        for (int x = toX; x >= startX; x--) {
            for (int y = toY; y >= startY; y--) {
                if (_selectedItemInfo != null) {
                    Log.warning("3 " + _selectedItemInfo.name);
                    ModuleHelper.getWorldModule().putObject(WorldHelper.getParcel(x, y), _selectedItemInfo, 10);
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
                JobModel job = JobHelper.createGatherJob(x, y);
                if (job != null) {
                    ModuleHelper.getJobModule().addJob(job);
                }
            }
        }
    }

    private void planCut(int startX, int startY, int toX, int toY) {
        for (int x = startX; x <= toX; x++) {
            for (int y = startY; y <= toY; y++) {
                JobModel job = JobHelper.createCutJob(x, y);
                if (job != null) {
                    ModuleHelper.getJobModule().addJob(job);
                }
            }
        }
    }

    public void planMining(int startX, int startY, int toX, int toY) {
        for (int x = startX; x <= toX; x++) {
            for (int y = startY; y <= toY; y++) {
                JobModel job = JobHelper.createMiningJob(x, y);
                if (job != null) {
                    ModuleHelper.getJobModule().addJob(job);
                }
            }
        }
    }

    public void planPick(int startX, int startY, int toX, int toY) {
//        for (int x = startX; x <= toX; x++) {
//            for (int y = startY; y <= toY; y++) {
//                ItemModel item = ModuleHelper.getWorldModule().getItem(x, y);
//                if (item != null) {
//                    JobModel job = JobTake.onCreate(item);
//                    if (job != null) {
//                        ModuleHelper.getJobModule().addJob(job);
//                    }
//                }
//            }
//        }
        throw new NotImplementedException();
    }

    public void planDestroy(int startX, int startY, int toX, int toY) {
        for (int x = startX; x <= toX; x++) {
            for (int y = startY; y <= toY; y++) {
                if (WorldHelper.getItem(x, y) != null) {
                    ModuleHelper.getJobModule().addJob(DumpJob.create(WorldHelper.getItem(x, y)));
                }
                if (WorldHelper.getStructure(x, y) != null) {
                    ModuleHelper.getJobModule().addJob(DumpJob.create(WorldHelper.getStructure(x, y)));
                }
            }
        }
    }

    public void planHaul(int startX, int startY, int toX, int toY) {
//        for (int x = startX; x <= toX; x++) {
//            for (int y = startY; y <= toY; y++) {
//                if (WorldHelper.getConsumable(x, y) != null && WorldHelper.getConsumable(x, y).getHaul() == null) {
//                    ModuleHelper.getJobModule().addJob(JobHaul.create(WorldHelper.getConsumable(x, y)));
//                }
//            }
//        }
    }

    public void plan(int startX, int startY, int toX, int toY) {
        if (_selectedPlan == null) {
            return;
        }

        switch (_selectedPlan) {
        case "destroy": planDestroy(startX, startY, toX, toY); break;
        case "gather": planGather(startX, startY, toX, toY); break;
        case "mine": planMining(startX, startY, toX, toY); break;
        case "pick": planPick(startX, startY, toX, toY); break;
        case "haul": planHaul(startX, startY, toX, toY); break;
        case "cut": planCut(startX, startY, toX, toY); break;
        default: break;
        }
    }

    public boolean isAction(Action action) {
        return _action.equals(action);
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
            case "build":
                UserInterface.getInstance().setCursor("base.cursor.build");
                break;
            case "gather":
                UserInterface.getInstance().setCursor("base.cursor.gather");
                break;
            case "mine":
                UserInterface.getInstance().setCursor("base.cursor.mine");
                break;
            case "cut":
                UserInterface.getInstance().setCursor("base.cursor.cut");
                break;
            case "destroy":
                UserInterface.getInstance().setCursor("base.cursor.destroy");
                break;
            case "haul":
                UserInterface.getInstance().setCursor("base.cursor.haul");
                break;
        }
    }

    public void set(Action action, ItemInfo info) {
        UserInterface.getInstance().setCursor("base.cursor.build");
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
                UserInterface.getInstance().setCursor(GameData.getData().getCursor("base.cursor.area"));
                break;
            case PUT_ITEM_FREE:
                break;
            case REMOVE_AREA:
                UserInterface.getInstance().setCursor(GameData.getData().getCursor("base.cursor.area"));
                break;
            case SET_PLAN:
                break;
        }
    }
}
