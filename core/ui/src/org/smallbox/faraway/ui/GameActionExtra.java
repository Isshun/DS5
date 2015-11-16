package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.job.model.DigJob;
import org.smallbox.faraway.core.game.module.job.model.DumpJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.engine.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GameActionExtra {
    private final Viewport              _viewport;
    private final GameSelectionExtra    _selector;
    private boolean                     _keyLeftPressed;
    private boolean                     _keyRightPressed;
    private int                         _keyPressPosX;
    private int                         _keyPressPosY;
    private int                         _keyMovePosX;
    private int                         _keyMovePosY;
    private boolean                     _mouseOnMap;
    private UICursor                    _cursor;
    private UISelection                 _selection;

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

    Action                              _action;
    int                                 _startPressX;
    int                                 _startPressY;
    int                                 _mouseMoveX;
    int                                 _mouseMoveY;
    GameEventListener.MouseButton       _button;
    private String                      _selectedPlan;
    private ItemInfo                    _selectedItemInfo;
    private AreaType                    _selectedAreaType;

    public GameActionExtra(Viewport viewport, GameSelectionExtra selector) {
        _viewport = viewport;
        _selector = selector;
        _startPressX = 0;
        _startPressY = 0;
        _mouseMoveX = 0;
        _mouseMoveY = 0;
        _button = null;
        _action = Action.NONE;
        _selection = new UISelection();
    }

    public int                      getMouseX() { return _keyMovePosX; }
    public int                      getMouseY() { return _keyMovePosY; }
    public int                      getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
    public int                      getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }

    public boolean onKeyLeft(int cursorX, int cursorY, int fromX, int fromY, int toX, int toY) {
        int floor = ModuleHelper.getWorldModule().getFloor();

        // Add area
        if (_action == Action.SET_AREA) {
            Application.getInstance().notify(gameObserver -> gameObserver.onAddArea(_selectedAreaType, fromX, fromY, toX, toY, floor));
            return true;
        }

        // Remove area
        if (_action == Action.REMOVE_AREA) {
            Application.getInstance().notify(gameObserver -> gameObserver.onRemoveArea(_selectedAreaType, fromX, fromY, toX, toY, floor));
            return true;
        }

        boolean consume = false;
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ParcelModel parcel = WorldHelper.getParcel(x, y);

                // Remove item
                if (_action == Action.REMOVE_ITEM) {
                    ModuleHelper.getWorldModule().takeItem(x, y);
                    consume = true;
                }

                // Set plan
                if (_action == Action.SET_PLAN) {
                    actionPlan(x, y, floor);
                    consume = true;
                }

                // Remove structure
                if (_action == Action.REMOVE_STRUCTURE) {
                    ModuleHelper.getWorldModule().removeStructure(x, y);
                    consume = true;
                }

                // Build item
                if (_action == Action.BUILD_ITEM) {
                    actionBuild(parcel);
                    consume = true;
                }

                // Build item free
                if (_action == Action.PUT_ITEM_FREE) {
                    // TODO
                    consume = true;
                }
            }
        }

        return consume;
    }

    public void onMoveEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        _keyMovePosX = getRelativePosX(x);
        _keyMovePosY = getRelativePosY(y);

        // Left click
        if (action == GameEventListener.Action.RELEASED && button == GameEventListener.MouseButton.LEFT) {
            if (_keyLeftPressed) {
                _keyLeftPressed = false;

                if (onKeyLeft(_keyPressPosX, _keyPressPosY,
                        Math.min(_keyPressPosX, _keyMovePosX),
                        Math.min(_keyPressPosY, _keyMovePosY),
                        Math.max(_keyPressPosX, _keyMovePosX),
                        Math.max(_keyPressPosY, _keyMovePosY))) {
                    return;
                }

                _selection.clear();

                // Check selection
                if (_selector.selectAt(
                        getRelativePosX(_selection.getFromX()),
                        getRelativePosY(_selection.getFromY()),
                        getRelativePosX(_selection.getToX()),
                        getRelativePosY(_selection.getToY()))) {
                    _selection.clear();
                    return;
                }

                // Select characters
                if (_action == GameActionExtra.Action.NONE) {
                    if (_selector.selectAt(getRelativePosX(x), getRelativePosY(y))) {
                        return;
                    }
                }
            }
        }

        if (action == GameEventListener.Action.PRESSED && button == GameEventListener.MouseButton.LEFT) {
            _keyLeftPressed = true;
            _keyMovePosX = _keyPressPosX = getRelativePosX(x);
            _keyMovePosY = _keyPressPosY = getRelativePosY(y);

            _selection.setStart(x, y);
        }

        if (action == GameEventListener.Action.RELEASED && button == GameEventListener.MouseButton.RIGHT) {
            _keyRightPressed = false;
        }

        if (action == GameEventListener.Action.PRESSED && button == GameEventListener.MouseButton.RIGHT) {
            _keyRightPressed = true;
        }

        if (action == GameEventListener.Action.MOVE) {
            if (_selector != null) {
                _selector.moveAt(getRelativePosX(x), getRelativePosY(y));
            }

            // TODO
            _mouseOnMap = x < 1500;

            // right button pressed
            if (_keyRightPressed || rightPressed) {
                _viewport.update(x, y);
                Log.debug("pos: " + _viewport.getPosX() + "x" + _viewport.getPosY());
//            if (_menu != null && _menu.isVisible()) {
//                //_menu.move(_viewport.getPosX(), _viewport.getPosY());
//                _menu.setViewPortPosition(_viewport.getPosX(), _viewport.getPosY());
//            }
            }

            if (_keyLeftPressed) {
                _selection.setPosition(x, y);
            }
        }
    }

    public void draw(GDXRenderer renderer) {
        if (_mouseOnMap && _cursor != null) {
            if (_keyLeftPressed) {
                _cursor.draw(renderer, _viewport,
                        Math.min(_keyPressPosX, _keyMovePosX),
                        Math.min(_keyPressPosY, _keyMovePosY),
                        Math.max(_keyPressPosX, _keyMovePosX),
                        Math.max(_keyPressPosY, _keyMovePosY),
                        true);
            } else {
                _cursor.draw(renderer, _viewport, _keyMovePosX, _keyMovePosY, _keyMovePosX, _keyMovePosY, false);
            }
        }

        if (_cursor == null) {
            renderer.draw(_selection, 100, 100);
        }
    }

    public Action  getAction() { return _action; }

    public void     actionBuild(ParcelModel parcel) {
        if (_selectedItemInfo == null) {
            return;
        }

        if (parcel != null) {
            // Check if rock is present on parcel
            if (parcel.hasRock()) {
                JobHelper.addMineJob(parcel.x, parcel.y, parcel.z, false);
            }

            // Check if plant is present on parcel
            if (parcel.hasPlant()) {
                JobHelper.addGatherJob(parcel.x, parcel.y, parcel.z, true);
            }

            if (_selectedItemInfo != null) {
                ModuleHelper.getWorldModule().putObject(parcel, _selectedItemInfo, 0);
            }
        }
    }

    public void planGather(int x, int y, int z) {
        JobModel job = JobHelper.createGatherJob(x, y, z);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

    private void planCut(int x, int y, int z) {
        JobModel job = JobHelper.createCutJob(x, y, z);
        if (job != null) {
            ModuleHelper.getJobModule().addJob(job);
        }
    }

    private void planCancel(int x, int y, int z) {
        ModuleHelper.getJobModule().getJobs().stream()
                .filter(job -> job.getJobParcel() == WorldHelper.getParcel(x, y, z))
                .forEach(job -> ModuleHelper.getJobModule().removeJob(job));
    }

    public void planMining(int x, int y, int z, DigMode mode) {
        if (mode == DigMode.RAMP_DOWN) {
            if (WorldHelper.hasRock(x, y, z - 1)) {
                DigJob job = JobHelper.createMiningJob(x, y, z - 1, true, WorldHelper.getParcel(x, y, z), Data.getData().getItemInfo("base.ground.link"));
                ModuleHelper.getJobModule().addJob(job);
            }
            if (WorldHelper.hasRock(x, y, z)) {
                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, false, WorldHelper.getParcel(x, y, z), Data.getData().getItemInfo("base.ground.link")));
            }
        }
        if (mode == DigMode.RAMP_UP) {
            if (WorldHelper.hasRock(x, y, z + 1)) {
                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z + 1, false, WorldHelper.getParcel(x, y, z + 1), Data.getData().getItemInfo("base.ground.link")));
            }
            if (WorldHelper.hasRock(x, y, z)) {
                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, true, WorldHelper.getParcel(x, y, z + 1), Data.getData().getItemInfo("base.ground.link")));
            }
        }
        if (mode == DigMode.HOLE) {
            if (WorldHelper.hasRock(x, y, z - 1)) {
                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z - 1, false, WorldHelper.getParcel(x, y, z), null));
            }
            if (WorldHelper.hasRock(x, y, z)) {
                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, false, WorldHelper.getParcel(x, y, z), null));
            }
        }
        if (mode == DigMode.FRONT) {
            if (WorldHelper.hasRock(x, y, z)) {
                ModuleHelper.getJobModule().addJob(JobHelper.createMiningJob(x, y, z, false, null, null));
            }
        }
    }

    public void planPick(int x, int y, int z) {
        throw new NotImplementedException();
    }

    public void planDestroy(ParcelModel parcel) {
        if (parcel.hasItem() && parcel.getItem().isComplete()) {
            ModuleHelper.getJobModule().addJob(DumpJob.create(parcel.getItem()));
        }
        if (parcel.hasStructure() && parcel.getStructure().isComplete()) {
            ModuleHelper.getJobModule().addJob(DumpJob.create(parcel.getStructure()));
        }
    }

    public void planHaul(int x, int y, int z) {
        throw new NotImplementedException();
    }

    public void actionPlan(int x, int y, int z) {
        if (_selectedPlan == null) {
            return;
        }

        switch (_selectedPlan) {
            case "destroy": planDestroy(WorldHelper.getParcel(x, y, z)); break;
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
        _action = Action.NONE;
        _selectedPlan = null;
        _selectedItemInfo = null;
        Game.getInstance().clearCursor();
    }

    public void set(Action action, String plan) {
        _action = action;
        _selectedPlan = plan;
        switch (plan) {
            case "build":
                Game.getInstance().setCursor("base.cursor.build");
                break;
            case "gather":
                Game.getInstance().setCursor("base.cursor.gather");
                break;
            case "dig":
            case "dig_hole":
            case "dig_ramp_up":
            case "dig_ramp_down":
                Game.getInstance().setCursor("base.cursor.dig");
                break;
            case "cut":
                Game.getInstance().setCursor("base.cursor.cut");
                break;
            case "destroy":
                Game.getInstance().setCursor("base.cursor.destroy");
                break;
            case "haul":
                Game.getInstance().setCursor("base.cursor.haul");
                break;
        }
    }

    public void set(Action action, ItemInfo info) {
        Game.getInstance().setCursor("base.cursor.build");
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
                Game.getInstance().setCursor(Data.getData().getCursor("base.cursor.area"));
                break;
            case PUT_ITEM_FREE:
                break;
            case REMOVE_AREA:
                Game.getInstance().setCursor(Data.getData().getCursor("base.cursor.area"));
                break;
            case SET_PLAN:
                break;
        }
    }
}