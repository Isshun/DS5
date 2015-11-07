package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.helper.JobHelper;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.job.model.DumpJob;
import org.smallbox.faraway.core.game.module.job.model.abs.JobModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class GameActionExtra {
    private final Viewport              _viewport;
    private final GameSelectionExtra _selector;
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

    public boolean onKeyLeft(int x, int y, int fromX, int fromY, int toX, int toY) {
        // Set plan
        if (_action == Action.SET_PLAN) {
            plan(fromX, fromY, toX, toY);
            return true;
        }

        // Set model
        if (_action == Action.SET_AREA) {
            Application.getInstance().notify(gameObserver -> gameObserver.onAddArea(_selectedAreaType, fromX, fromY, toX, toY));
            return true;
        }

        // Set model
        if (_action == Action.REMOVE_AREA) {
            Application.getInstance().notify(gameObserver -> gameObserver.onRemoveArea(_selectedAreaType, fromX, fromY, toX, toY));
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

    public void    planBuild(int startX, int startY, int toX, int toY) {
        if (_selectedItemInfo == null) {
            return;
        }

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

                    if (_selectedItemInfo != null) {
                        ModuleHelper.getWorldModule().putObject(parcel, _selectedItemInfo, 0);
                    }
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
            case "mine":
                Game.getInstance().setCursor("base.cursor.mine");
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