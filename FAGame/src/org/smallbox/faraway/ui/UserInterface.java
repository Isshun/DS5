package org.smallbox.faraway.ui;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.GFXRenderer;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.SpriteManager;
import org.smallbox.faraway.engine.Viewport;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.CharacterManager;
import org.smallbox.faraway.game.manager.TemperatureManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.ui.cursor.BuildCursor;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.*;
import org.smallbox.faraway.ui.panel.debug.OxygenManagerPanel;
import org.smallbox.faraway.ui.panel.debug.TemperatureManagerPanel;
import org.smallbox.faraway.ui.panel.info.*;
import org.smallbox.faraway.ui.panel.right.*;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

public class UserInterface implements GameEventListener {
    private static UserInterface		_self;
    private final LayoutFactory         _factory;
    private final ViewFactory           _viewFactory;
    private Viewport                    _viewport;
    private boolean						_keyLeftPressed;
    private boolean						_keyRightPressed;
    private int							_keyPressPosX;
    private int							_keyPressPosY;
    private int							_keyMovePosX;
    private int							_keyMovePosY;
    private UserInteraction				_interaction;
    private CharacterManager            _characters;
    private Mode 						_mode;
    private ContextualMenu 				_menu;
    private Game 						_game;
    private boolean 					_mouseOnMap;
    private BasePanel 					_currentPanel;
    private UserInterfaceCursor			_cursor;
    private long 						_lastLeftClick;
    private int 						_lastInput;
    private PanelConsole                _panelConsole;
    private UserInterfaceSelector       _selector;
    private int 						_update;
    private long                        _lastModified;

    private	BasePanel[]					_panels = new BasePanel[] {
            new PanelSystem(),
            new PanelResources(),
            new PanelDebug(),

            new PanelQuest(),
            new PanelCharacter(	    Mode.CHARACTER,         null),
//            new PanelInfo(		    Mode.INFO, 		        null),
            new PanelInfoStructure(	Mode.INFO_STRUCTURE, 	null),
            new PanelInfoItem(	    Mode.INFO_ITEM, 	    null),
            new PanelInfoConsumable(Mode.INFO_CONSUMABLE,   null),
            new PanelInfoParcel(	Mode.INFO_PARCEL, 	    null),
            new PanelInfoArea(	    Mode.INFO_AREA, 	    null),
            new PanelInfoResource(	Mode.INFO_RESOURCE, 	null),
            new PanelInfoAnimal(	Mode.INFO_ANIMAL, 	    null),
            new PanelPlan(		    Mode.PLAN, 		        Key.P),
//            new PanelRoom(		    Mode.ROOM, 		        Key.R),
            new PanelTooltip(	    Mode.TOOLTIP, 	        Key.F1),
            new PanelBuild(		    Mode.BUILD, 	        Key.B),
            new PanelScience(	    Mode.SCIENCE, 	        null),
            new PanelSecurity(	    Mode.SECURITY, 	        null),
            new PanelCrew(		    Mode.CREW, 		        Key.C),
            new PanelJobs(		    Mode.JOBS, 		        Key.O),
            new PanelArea(		    Mode.AREA, 		        Key.A),
//			new PanelStats(		    Mode.STATS, 	        Key.S),
            new PanelManager(	    Mode.MANAGER, 	        Key.M),
            new PanelShortcut(	    Mode.NONE, 		        null),
            new PanelPlanet(),
            new PanelTopInfo(),
            new PanelTopRight(),

            // Manager debug
            new TemperatureManagerPanel(),
            new OxygenManagerPanel(),
    };

    public void reloadTemplates() {
        // Refresh UI if needed by GameData (strings)
        if (GameData.getData().needUIRefresh) {
            GameData.getData().needUIRefresh = false;
            reload();
        }

        // Refresh UI if needed by UI files
        long lastResModified = Utils.getLastUIModified();
        if (lastResModified > _lastModified) {
            _lastModified = lastResModified;
            reload();
        }
    }

    public BasePanel[] getPanels() {
        return _panels;
    }

    public enum Mode {
        INFO,
        DEBUG,
        BUILD,
        CREW,
        JOBS,
        CHARACTER,
        SCIENCE,
        SECURITY,
        ROOM,
        PLAN,
        DEBUGITEMS,
        NONE,
        TOOLTIP,
        STATS,
        INFO_STRUCTURE, INFO_ITEM, INFO_PARCEL, INFO_RESOURCE, INFO_CONSUMABLE, AREA, INFO_AREA, INFO_ANIMAL, MANAGER
    }

    public UserInterface(LayoutFactory layoutFactory, ViewFactory viewFactory) {
        _self = this;
        _factory = layoutFactory;
        _viewFactory = viewFactory;
        _interaction = new UserInteraction(this);
        _selector = new UserInterfaceSelector(this);
        _panelConsole = new PanelConsole();
        _panelConsole.init(viewFactory, layoutFactory, this, _interaction, null);
    }

    @Override
    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        if (action == Action.RELEASED) {
            if (checkKeyboard(key, _lastInput)) {
                return;
            }
        }
    }

    @Override
    public void onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (action == Action.MOVE) {
            if (_currentPanel != null) {
                _currentPanel.onMouseMove(x, y);
            }
            onMouseMove(x, y, rightPressed);
            UIEventManager.getInstance().onMouseMove(x, y);
        }

        for (BasePanel panel: _panels) {
            if (panel.isVisible() && panel.onMouseEvent(action, button, x, y)) {
                return;
            }
        }

        if (action == Action.PRESSED || action == Action.RELEASED) {
            switch (button) {
                case LEFT:
                    if (action == Action.PRESSED) {
                        onLeftPress(x, y);
                    } else {
                        // Is consume by EventManager
                        if (UIEventManager.getInstance().click(x, y)) {
                            // Nothing to do !
                        }
                        // Is double click
                        else if (_lastLeftClick + 200 > System.currentTimeMillis()) {
                            onDoubleClick(x, y);
                        }
                        // Is simple click
                        else {
                            boolean use = onLeftClick(x, y);
                            if (use) {
                                onRefresh(_update);
                            }
                        }
                        _lastLeftClick = System.currentTimeMillis();
                    }
                    break;

                case MIDDLE:
                    break;

                case RIGHT:
                    if (action == Action.PRESSED) {
                        onRightPress(x, y);
                    } else {
                        if (UIEventManager.getInstance().rightClick(x, y)) {
                            // Is consume by EventManager
                            // Nothing to do !
                        } else {
                            onRightClick(x, y);
                        }
                    }
                    break;
            }
            //_ui.mouseRelease(event.asMouseButtonEvent().button, event.asMouseButtonEvent().position.x, event.asMouseButtonEvent().position.y);
        }

        // TODO
//        if (event.type == Event.Type.MOUSE_WHEEL_MOVED) {
//            onMouseWheel(event.asMouseWheelEvent().delta, event.asMouseWheelEvent().position.x, event.asMouseWheelEvent().position.y);
//        }
    }

    @Override
    public void onWindowEvent(Action action) {
    }

    public void reload() {
        for (BasePanel panel: _panels) {
            panel.removeAllViews();
            panel.init(_viewFactory, _factory, this, _interaction, null);
            panel.refresh(0);
        }
    }

    public BasePanel getPanel(Class<? extends BasePanel> panelCls) {
        for (BasePanel panel: _panels) {
            if (panel.getClass() == panelCls) {
                return panel;
            }
        }
        return null;
    }

    public void putDebug(ItemInfo itemInfo) {
        _interaction.set(UserInteraction.Action.PUT_ITEM_FREE, itemInfo);
        setCursor(new BuildCursor());
    }

    public void setCursor(UserInterfaceCursor cursor) {
        _cursor = cursor;
    }

    public void onCreate(Game game) {
        _game = game;
        _viewport = game.getViewport();
        _characters = Game.getCharacterManager();
        _keyLeftPressed = false;
        _keyRightPressed = false;

        for (BasePanel panel: _panels) {
            panel.init(_viewFactory, _factory, this, _interaction, SpriteManager.getInstance().createRenderEffect());
            _game.addObserver(panel);
        }

        setMode(Mode.NONE);
    }

    public void	onMouseMove(int x, int y, boolean rightPressed) {
        _keyMovePosX = getRelativePosX(x);
        _keyMovePosY = getRelativePosY(y);

        // TODO
        _mouseOnMap = x < 1500;

        // right button pressed
        if (_keyRightPressed || rightPressed) {
            _viewport.update(x, y);
            Log.debug("pos: " + _viewport.getPosX() + "x" + _viewport.getPosY());
            if (_menu != null && _menu.isVisible()) {
                //_menu.move(_viewport.getPosX(), _viewport.getPosY());
                _menu.setViewPortPosition(_viewport.getPosX(), _viewport.getPosY());
            }
        }
    }

    public void	onLeftPress(int x, int y) {
        if (UIEventManager.getInstance().has(x, y)) {
            return;
        }

        for (BasePanel panel: _panels) {
            if (panel.catchClick(x, y)) {
                _keyLeftPressed = false;
                return;
            }
        }

        _keyLeftPressed = true;
        _keyMovePosX = _keyPressPosX = getRelativePosX(x);
        _keyMovePosY = _keyPressPosY = getRelativePosY(y);
    }

    public void	onRightPress(int x, int y) {
        if (UIEventManager.getInstance().has(x, y)) {
            return;
        }

        _keyRightPressed = true;
    }

    public UserInterfaceSelector getSelector() { return _selector; }
    public int 				getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
    //    public int 				getRelativePosXMax(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
//    public int 				getRelativePosYMax(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
//    public int 				getRelativePosXMin(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
//    public int 				getRelativePosYMin(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }
    public int				getMouseX() { return _keyMovePosX; }
    public int				getMouseY() { return _keyMovePosY; }

    public void toggleMode(Mode mode) {
        setMode(_mode != mode ? mode : Mode.NONE);
    }

    public void setMode(Mode mode) {
        _interaction.clean();

        _mode = mode;
        _menu = null;

        if (mode == Mode.NONE) {
            _interaction.clean();
            _selector.clean();
        }

        for (BasePanel panel: _panels) {
            if (_mode.equals(panel.getMode())) {
                _currentPanel = panel;
                panel.setVisible(true);
            } else if (!panel.isAlwaysVisible()) {
                panel.setVisible(false);
            }
        }

        if (_currentPanel != null) {
            _currentPanel.setVisible(true);
        }
    }

    public void	onMouseWheel(int delta, int x, int y) {
        _viewport.setScale(delta, x, y);
    }

    public void onRefresh(int update) {
        _update = update;
        for (BasePanel panel: _panels) {
            panel.refresh(update);
        }
        _panelConsole.refresh(update);
    }

    public void onDraw(GFXRenderer renderer, int update, long renderTime) {
        for (BasePanel panel: _panels) {
            panel.draw(renderer, null);
        }

        //_panelConsole.draw(renderer, null);

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

        if (_menu != null) {
            _menu.draw(renderer, null);
        }
    }

    public boolean checkKeyboard(Key key, int lastInput) {

        for (BasePanel panel: _panels) {
            if (panel.isVisible() && panel.checkKey(key)) {
                return true;
            }
        }

        switch (key) {

            case ADD:
                if (Application.getUpdateInterval() - 40 > 0) {
                    Application.setUpdateInterval(Application.getUpdateInterval() - 40);
                } else {
                    Application.setUpdateInterval(0);
                }
                return true;

            case SUBTRACT:
                Application.setUpdateInterval(Application.getUpdateInterval() + 40);
                return true;

            case ESCAPE:
                setMode(Mode.NONE);
                return true;

            case BACKSPACE:
                return true;

            case TAB:
                if (_selector.getSelectedCharacter() != null) {
                    _selector.select(_characters.getNext(_selector.getSelectedCharacter()));
                }
                return true;

            case SPACE:
                _game.setRunning(!_game.isRunning());
                return true;

            default: break;
        }

        for (BasePanel panel: _panels) {
            if (key.equals(panel.getShortcut())) {
                toggleMode(panel.getMode());
                return true;
            }
        }

        return false;
    }

    public static UserInterface getInstance() {
        return _self;
    }

    public void onDoubleClick(int x, int y) {
//        _keyLeftPressed = false;
//
//        ParcelModel area = Game.getWorldManager().getParcel(getRelativePosX(x), getRelativePosY(y));
//        if (area != null) {
//            ItemModel item = area.getItem();
//            StructureModel structure = area.getStructure();
//
//            if (item != null) {
//                item.nextMode();
//                Game.getInstance().notify(observer -> observer.onRefreshItem(item));
//            }
//            else if (structure != null) {
//                structure.nextMode();
//                Game.getInstance().notify(observer -> observer.onRefreshStructure(structure));
//            }
//        }
    }

    public boolean onLeftClick(int x, int y) {
        if (!_keyLeftPressed) {
            return false;
        }
        _keyLeftPressed = false;

        // Check user actions
        if (_interaction.onKeyLeft(
                _keyPressPosX, _keyPressPosY,
                Math.min(_keyPressPosX, _keyMovePosX),
                Math.min(_keyPressPosY, _keyMovePosY),
                Math.max(_keyPressPosX, _keyMovePosX),
                Math.max(_keyPressPosY, _keyMovePosY))) {
            return true;
        }

        // Click is catch by panel
        if (_currentPanel != null && _currentPanel.catchClick(x, y)) {
            return true;
        }

        // Select characters
        if (_interaction.isAction(UserInteraction.Action.NONE)) {
            if (_selector.selectAt(getRelativePosX(x), getRelativePosY(y))) {
                return true;
            }
        }

        return false;
    }

    public void onRightClick(int x, int y) {
        // Cancel selected items
        if (_mode == Mode.CHARACTER) {
            setMode(Mode.CREW);
            return;
        }
        _interaction.clean();
        _cursor = null;
        toggleMode(Mode.NONE);

        _keyRightPressed = false;
    }

    public Mode getMode() {
        return _mode;
    }

    public void addMessage(int level, String message) {
        _panelConsole.addMessage(level, message);
    }

    public void back() {
        setMode(Mode.NONE);
    }

}
