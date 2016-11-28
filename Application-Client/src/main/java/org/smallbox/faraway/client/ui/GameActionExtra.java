package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.MouseEvent;
import org.smallbox.faraway.client.ui.engine.UICursor;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.area.model.AreaType;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GameActionExtra {
    private final Viewport              _viewport;
    private final GameSelectionExtra    _selector;

    private UICursor _cursor;
    private MouseEvent _mouseEvent;

    public void setCursor(UICursor cursor) {
        _cursor = cursor;
    }

    public boolean isClear() {
        return _action == Action.NONE;
    }

    public enum Action {
        NONE, REMOVE_ITEM, REMOVE_STRUCTURE, BUILD_ITEM, SET_AREA, PUT_ITEM_FREE, REMOVE_AREA, SET_PLAN
    }

    public enum DigMode {
        FRONT, RAMP_UP, RAMP_DOWN, HOLE
    }

//    public int                      getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
//    public int                      getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }

    Action                              _action;
    int                                 _startPressX;
    int                                 _startPressY;
    int                                 _mouseMoveX;
    int                                 _mouseMoveY;
    GameEventListener.MouseButton       _button;
    private String                      _selectedPlan;

    public GameActionExtra(Viewport viewport, GameSelectionExtra selector) {
        _mouseEvent = new MouseEvent();
        _viewport = viewport;
        _selector = selector;
        _startPressX = 0;
        _startPressY = 0;
        _mouseMoveX = 0;
        _mouseMoveY = 0;
        _button = null;
        _action = Action.NONE;
    }

//    public int                      getMouseX() { return _keyMovePosX; }
//    public int                      getMouseY() { return _keyMovePosY; }

    public void onMoveEvent(GameEvent event, GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        _mouseEvent.consumed = false;
        _mouseEvent.x = x;
        _mouseEvent.y = y;
        _mouseEvent.button = button;
        _mouseEvent.action = action;

        // Left click
        if (action == GameEventListener.Action.RELEASED) {
            if (!event.consumed) {
                Application.notify(obs -> obs.onMouseRelease(event));
            }

            if (!event.consumed && _mouseEvent.x < 1500) {
                Application.notify(obs -> obs.onClickOnMap(event));
            }
        }

        if (action == GameEventListener.Action.PRESSED) {
            if (!event.consumed) {
                Application.notify(obs -> obs.onMousePress(event));
            }
        }

        if (action == GameEventListener.Action.MOVE) {
            if (!event.consumed) {
                Application.notify(observer -> observer.onMouseMove(event));
            }
        }
    }

    // TODO
//    public void draw(GDXRenderer renderer) {
//        if (_mouseOnMap && _cursor != null) {
//            if (_keyLeftPressed) {
//                _cursor.draw(renderer, _viewport,
//                        Math.min(_keyPressPosX, _keyMovePosX),
//                        Math.min(_keyPressPosY, _keyMovePosY),
//                        Math.max(_keyPressPosX, _keyMovePosX),
//                        Math.max(_keyPressPosY, _keyMovePosY),
//                        true);
//            } else {
//                _cursor.draw(renderer, _viewport, _keyMovePosX, _keyMovePosY, _keyMovePosX, _keyMovePosY, false);
//            }
//        }
//
//        if (_cursor == null) {
//            renderer.draw(_selection, 100, 100);
//        }
//    }

    public Action  getAction() { return _action; }

    public void     actionBuild(ParcelModel parcel) {
        throw new org.apache.commons.lang3.NotImplementedException("");

//        if (_selectedItemInfo == null) {
//            return;
//        }
//
//        if (parcel != null) {
//            // Check if rock is present on parcel
//            if (parcel.hasRock()) {
//                JobHelper.addMineJob(parcel.x, parcel.y, parcel.z, false);
//            }
//
//            // Check if plant is present on parcel
//            if (parcel.hasPlant()) {
//                JobHelper.addGatherJob(parcel.x, parcel.y, parcel.z, true);
//            }
//
//            if (_selectedItemInfo != null) {
//                ModuleHelper.getWorldModule().putObject(parcel, _selectedItemInfo, 0);
//            }
//        }
    }

    public void planGather(int x, int y, int z) {
        throw new org.apache.commons.lang3.NotImplementedException("");

//        JobModel job = JobHelper.createGatherJob(x, y, z);
//        if (job != null) {
//            ModuleHelper.getJobModule().addJob(job);
//        }
    }

    private void planCut(int x, int y, int z) {
        throw new org.apache.commons.lang3.NotImplementedException("");

//        JobModel job = JobHelper.createCutJob(x, y, z);
//        if (job != null) {
//            ModuleHelper.getJobModule().addJob(job);
//        }
    }

    private void planCancel(int x, int y, int z) {
        Application.notify(obs -> obs.onCancelJobs(WorldHelper.getParcel(x, y, z), null));
    }

    public void planMining(int x, int y, int z, DigMode mode) {
        throw new org.apache.commons.lang3.NotImplementedException("");

//        if (mode == DigMode.RAMP_DOWN) {
//            if (WorldHelper.hasRock(x, y, z - 1)) {
//                DigJob job = JobHelper.createMiningJob(x, y, z - 1, true, WorldHelper.getParcel(x, y, z), Application.data.getItemInfo("base.ground.link"));
//                ModuleHelper.getJobModule().addJob(job);
//            }
//            if (WorldHelper.hasRock(x, y, z)) {
//                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, false, WorldHelper.getParcel(x, y, z), Application.data.getItemInfo("base.ground.link")));
//            }
//        }
//        if (mode == DigMode.RAMP_UP) {
//            if (WorldHelper.hasRock(x, y, z + 1)) {
//                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z + 1, false, WorldHelper.getParcel(x, y, z + 1), Application.data.getItemInfo("base.ground.link")));
//            }
//            if (WorldHelper.hasRock(x, y, z)) {
//                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, true, WorldHelper.getParcel(x, y, z + 1), Application.data.getItemInfo("base.ground.link")));
//            }
//        }
//        if (mode == DigMode.HOLE) {
//            if (WorldHelper.hasRock(x, y, z - 1)) {
//                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z - 1, false, WorldHelper.getParcel(x, y, z), null));
//            }
//            if (WorldHelper.hasRock(x, y, z)) {
//                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, false, WorldHelper.getParcel(x, y, z), null));
//            }
//        }
//        if (mode == DigMode.FRONT) {
//            if (WorldHelper.hasRock(x, y, z)) {
//                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, false, null, null));
//            }
//        }
    }

    public void planPick(int x, int y, int z) {
        throw new NotImplementedException();
    }

    public void planDestroy(ParcelModel parcel) {
        throw new org.apache.commons.lang3.NotImplementedException("");

//        if (parcel.hasItem() && parcel.getItem().isComplete()) {
//            ModuleHelper.getJobModule().addJob(DumpJob.onCreateJob(parcel.getItem()));
//        }
//        if (parcel.hasStructure() && parcel.getStructure().isComplete()) {
//            ModuleHelper.getJobModule().addJob(DumpJob.onCreateJob(parcel.getStructure()));
//        }
    }

    public void planHaul(int x, int y, int z) {
        throw new NotImplementedException();
    }

    public void actionPlan(int x, int y, int z) {
        if (_selectedPlan == null) {
            return;
        }

        switch (_selectedPlan) {
            case "unload": planDestroy(WorldHelper.getParcel(x, y, z)); break;
            case "gather": planGather(x, y, z); break;
            case "dig": planMining(x, y, z, DigMode.FRONT); break;
            case "dig_hole": planMining(x, y, z, DigMode.HOLE); break;
            case "dig_ramp_up": planMining(x, y, z, DigMode.RAMP_UP); break;
            case "dig_ramp_down": planMining(x, y, z, DigMode.RAMP_DOWN); break;
            case "pick": planPick(x, y, z); break;
            case "haul": planHaul(x, y, z); break;
            case "cut": planCut(x, y, z); break;
            case "cancel": planCancel(x, y, z); break;
            default: break;
        }
    }

    public boolean isAction(Action action) {
        return _action.equals(action);
    }

    public void clear() {
        throw new NotImplementedException();

//        _action = Action.NONE;
//        _selectedPlan = null;
//        _selectedItemInfo = null;
//        Application.gameManager.getGame().clearCursor();
    }

    public void set(Action action, String plan) {
        _action = action;
        _selectedPlan = plan;
        // TODO
        //        switch (plan) {
//            case "build":
//                Application.gameManager.getGame().setCursor("base.cursor.build");
//                break;
//            case "gather":
//                Application.gameManager.getGame().setCursor("base.cursor.gather");
//                break;
//            case "dig":
//            case "dig_hole":
//            case "dig_ramp_up":
//            case "dig_ramp_down":
//                Application.gameManager.getGame().setCursor("base.cursor.dig");
//                break;
//            case "cut":
//                Application.gameManager.getGame().setCursor("base.cursor.cut");
//                break;
//            case "unload":
//                Application.gameManager.getGame().setCursor("base.cursor.unload");
//                break;
//            case "haul":
//                Application.gameManager.getGame().setCursor("base.cursor.haul");
//                break;
//            case "cancel":
//                Application.gameManager.getGame().setCursor("base.cursor.cancel");
//                break;
//        }
    }

    public void set(Action action, ItemInfo info) {
        throw new NotImplementedException();

//        Application.gameManager.getGame().setCursor("base.cursor.build");
//        _action = action;
//        _selectedItemInfo = info;
    }

    public void set(Action action, AreaType areaType) {
        throw new NotImplementedException();

//        _action = action;
//        _selectedAreaType = areaType;
//
//        switch (action) {
//            case NONE:
//                break;
//            case REMOVE_ITEM:
//                break;
//            case REMOVE_STRUCTURE:
//                break;
//            case BUILD_ITEM:
//                break;
//            case SET_AREA:
//                Application.gameManager.getGame().setCursor(Application.data.getCursor("base.cursor.area"));
//                break;
//            case PUT_ITEM_FREE:
//                break;
//            case REMOVE_AREA:
//                Application.gameManager.getGame().setCursor(Application.data.getCursor("base.cursor.area"));
//                break;
//            case SET_PLAN:
//                break;
//        }
    }
}