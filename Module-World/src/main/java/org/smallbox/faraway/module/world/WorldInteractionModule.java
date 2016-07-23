package org.smallbox.faraway.module.world;

import org.apache.commons.lang3.NotImplementedException;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.MouseEvent;
import org.smallbox.faraway.ui.UISelection;

import java.util.ArrayList;
import java.util.List;

public class WorldInteractionModule extends GameModule<WorldInteractionModuleObserver> {
    @BindModule("")
    private WorldModule _world;

    private GameActionExtra.Action      _action;
    private int                         _keyPressPosX;
    private int                         _keyPressPosY;
    private int                         _keyPressPosZ;
    private int                         _keyMovePosX;
    private int                         _keyMovePosY;
    private UISelection                 _selection;
    private boolean                     _keyLeftPressed;
    private boolean                     _keyRightPressed;
    private ParcelModel                 _lastMoveParcel;
    private ItemInfo                    _selectedItemInfo;
    private AreaType                    _selectedAreaType;
    private boolean                     _mouseOnMap;

    @Override
    public boolean isModuleMandatory() {
        return true;
    }

    @Override
    public void onMouseMove(MouseEvent event) {
        // right button pressed
        if (_keyRightPressed) {
            Game.getInstance().getViewport().update(event.x, event.y);
            Log.debug("pos: %d x %d", Game.getInstance().getViewport().getPosX(), Game.getInstance().getViewport().getPosY());
//            if (_menu != null && _menu.isVisible()) {
//                //_menu.move(_viewport.getPosX(), _viewport.getPosY());
//                _menu.setViewPortPosition(_viewport.getPosX(), _viewport.getPosY());
//            }
        }
    }

    @Override
    protected void onGameCreate(Game game) {
        _selection = new UISelection();

        _world.addObserver(new WorldModuleObserver() {
            @Override
            public void onMouseMove(MouseEvent event, int parcelX, int parcelY, int floor) {
                _keyMovePosX = parcelX;
                _keyMovePosY = parcelY;

                ParcelModel parcel = _world.getParcel(parcelX, parcelY, floor);
                if (_lastMoveParcel != parcel) {
                    _lastMoveParcel = parcel;
                    _world.notifyObservers(obs -> obs.onOverParcel(parcel));
                }

                if (_keyLeftPressed) {
                    _selection.setPosition(parcelX, parcelY, WorldHelper.getCurrentFloor());
                }
            }

            @Override
            public void onMousePress(MouseEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {
                if (button == GameEventListener.MouseButton.LEFT) {
                    _keyLeftPressed = true;

                    _keyMovePosX = _keyPressPosX = parcelX;
                    _keyMovePosY = _keyPressPosY = parcelY;
                    _keyPressPosZ = WorldHelper.getCurrentFloor();

                    _selection.setStart(parcelX, parcelY, WorldHelper.getCurrentFloor());
                }

                if (button == GameEventListener.MouseButton.RIGHT) {
                    _keyRightPressed = true;
                }
            }

            @Override
            public void onMouseRelease(MouseEvent event, int parcelX, int parcelY, int floor, GameEventListener.MouseButton button) {
                if (button == GameEventListener.MouseButton.LEFT) {
                    if (_keyLeftPressed) {
                        _keyLeftPressed = false;

                        if (onKeyLeft(_keyPressPosX, _keyPressPosY,
                                Math.min(_keyPressPosX, _keyMovePosX),
                                Math.min(_keyPressPosY, _keyMovePosY),
                                Math.max(_keyPressPosX, _keyMovePosX),
                                Math.max(_keyPressPosY, _keyMovePosY))) {
                            return;
                        }

                        Game.getInstance().getSelector().clear();

                        // Check selection
                        if (selectAt(
                                _selection.getFromX(),
                                _selection.getFromY(),
                                _selection.getFromZ(),
                                _selection.getToX(),
                                _selection.getToY(),
                                _selection.getToZ())) {
                            _selection.clear();
                            return;
                        }

                        // Select characters
                        if (_action == GameActionExtra.Action.NONE) {
                            if (selectAt(parcelX, parcelY, WorldHelper.getCurrentFloor())) {
                                return;
                            }
                        }
                    }
                }

                if (button == GameEventListener.MouseButton.RIGHT) {
                    _keyRightPressed = false;
                }
            }
        });
    }

    public boolean selectAt(int x, int y, int z) {
        throw new NotImplementedException("");

//        Application.getInstance().notify(GameObserver::onDeselect);
//
//        ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(x, y, z);
//        if (parcel != null) {
//            for (GameModule module: ModuleManager.getInstance().getGameModules()) {
//                try {
//                    if (module.onSelectParcel(parcel)) {
//                        return true;
//                    }
//                } catch (Exception e) {
//                     //TODO
//                    Log.error(e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        return false;
    }

    public boolean selectAt(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        Application.getInstance().notify(GameObserver::onDeselect);

        notifyObservers(obs -> obs.onSelect(fromX, fromY, fromZ, toX, toY, toZ));

        List<ParcelModel> parcels = new ArrayList<>();
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    parcels.add(_world.getParcel(x, y, z));
                }
            }
        }
        notifyObservers(obs -> obs.onSelect(parcels));

        return false;
    }

    public boolean onKeyLeft(int cursorX, int cursorY, int fromX, int fromY, int toX, int toY) {
        int floor = WorldHelper.getCurrentFloor();

        // Add area
        if (_action == GameActionExtra.Action.SET_AREA) {
            Application.getInstance().notify(gameObserver -> gameObserver.onAddArea(_selectedAreaType, fromX, fromY, toX, toY, floor));
            return true;
        }

        // Remove area
        if (_action == GameActionExtra.Action.REMOVE_AREA) {
            Application.getInstance().notify(gameObserver -> gameObserver.onRemoveArea(_selectedAreaType, fromX, fromY, toX, toY, floor));
            return true;
        }

        boolean consume = false;
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                ParcelModel parcel = WorldHelper.getParcel(x, y, WorldHelper.getCurrentFloor());

                // Remove item
                if (_action == GameActionExtra.Action.REMOVE_ITEM) {
                    // TODO
//                    _world.takeItem(x, y, WorldHelper.getCurrentFloor());
                    consume = true;
                }

                // Set plan
                if (_action == GameActionExtra.Action.SET_PLAN) {
                    int finalX = x;
                    int finalY = y;
                    notifyObservers(obs -> obs.actionPlan(finalX, finalY, floor));
                    consume = true;
                }

                // TODO
                // Remove structure
                if (_action == GameActionExtra.Action.REMOVE_STRUCTURE) {
//                    Application.getInstance().notify(observer -> observer.);
//                    ModuleHelper.getWorldModule().removeStructure(x, y, WorldHelper.getCurrentFloor());

//                    public void removeStructure(int x, int y, int z) {
//                        if (!WorldHelper.inMapBounds(x, y, z)) {
//                            return;
//                        }
//
//                        StructureModel structure = _parcels[x][y][z].getStructure();
//                        if (structure != null) {
//                            if (structure.getParcel().getStructure() == structure) {
//                                structure.getParcel().setStructure(null);
//                            }
//
//                            _structures.remove(structure);
//                            Application.getInstance().notify(observer -> observer.onRemoveStructure(_parcels[x][y][z], structure));
//                        }
//                    }

                    consume = true;
                }

                // Build item
                if (_action == GameActionExtra.Action.BUILD_ITEM) {
                    notifyObservers(obs -> obs.actionBuild(parcel));
                    consume = true;
                }

                // Build item free
                if (_action == GameActionExtra.Action.PUT_ITEM_FREE) {
                    // TODO
                    consume = true;
                }
            }
        }

        return consume;
    }

    @Override
    public void onGameStart(Game game) {
    }

    @Override
    public void onGameUpdate(Game game, int tick) {
    }

}
