package org.smallbox.faraway.ui;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.CharacterManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ItemModel;
import org.smallbox.faraway.game.model.item.MapObjectModel;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.cursor.BuildCursor;
import org.smallbox.faraway.ui.cursor.DefaultCursor;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.UIMessage;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.*;
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
    private UIMessage 					_message;
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
            new PanelDev(),
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
            new PanelDebug(		    Mode.DEBUG, 	        Key.TILDE),
            new PanelPlan(		    Mode.PLAN, 		        Key.P),
            new PanelRoom(		    Mode.ROOM, 		        Key.R),
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
        _cursor = new DefaultCursor();

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
    public int 				getRelativePosXMax(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosYMax(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
    public int 				getRelativePosXMin(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosYMin(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }
    public int				getMouseX() { return _keyMovePosX; }
    public int				getMouseY() { return _keyMovePosY; }

    public void toggleMode(Mode mode) {
        setMode(_mode != mode ? mode : Mode.NONE);
    }

    public void setMode(Mode mode) {
        _interaction.clean();

        ((MainRenderer)MainRenderer.getInstance()).setMode(mode);

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

//		_keyMovePosX = getRelativePosX(-_viewport.getPosX());
//		_keyMovePosY = getRelativePosY(-_viewport.getPosY());

//		MainRenderer.getInstance().invalidate();
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

        if (_mouseOnMap) {
            if (_interaction.isAction(UserInteraction.Action.SET_ROOM)
                    || _interaction.isAction(UserInteraction.Action.PUT_ITEM_FREE)
                    || _interaction.isAction(UserInteraction.Action.SET_AREA)
                    || _interaction.isAction(UserInteraction.Action.REMOVE_AREA)
                    || _interaction.isAction(UserInteraction.Action.SET_PLAN)
                    || _interaction.isAction(UserInteraction.Action.BUILD_ITEM)) {
                if (_keyLeftPressed) {
                    _cursor.draw(renderer, _viewport, Math.min(_keyPressPosX, _keyMovePosX),
                            Math.min(_keyPressPosY, _keyMovePosY),
                            Math.max(_keyPressPosX, _keyMovePosX),
                            Math.max(_keyPressPosY, _keyMovePosY));
                } else {
                    _cursor.draw(renderer, _viewport, Math.min(_keyMovePosX, _keyMovePosX),
                            Math.min(_keyMovePosY, _keyMovePosY),
                            Math.max(_keyMovePosX, _keyMovePosX),
                            Math.max(_keyMovePosY, _keyMovePosY));
                }
            }
        }

        if (_message != null && renderer != null && _viewport != null) {
            _message.border.draw(renderer, _viewport.getRenderEffect());
            _message.shape.draw(renderer, _viewport.getRenderEffect());
            _message.text.draw(renderer, _viewport.getRenderEffect());
            if (--_message.frame < 0) {
                _message = null;
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

    public void displayMessage(String msg) {
        //		_message = new UIMessage(msg, _mouseRealPosX, _mouseRealPosY);
        _message = new UIMessage(msg, 10, 30);
    }

    public void displayMessage(String msg, int x, int y) {
//		_message = new UIMessage(msg, _viewport.getRealPosX(x) + 20, _viewport.getRealPosY(y) + 12);
        _message = new UIMessage(msg, x * Constant.TILE_WIDTH + 20, y * Constant.TILE_HEIGHT + 12);
    }

    public void onDoubleClick(int x, int y) {
        _keyLeftPressed = false;

        ParcelModel area = Game.getWorldManager().getParcel(getRelativePosX(x), getRelativePosY(y));
        if (area != null) {
            MapObjectModel item = area.getItem();
            MapObjectModel structure = area.getStructure();

            if (item != null) {
                item.nextMode();
                MainRenderer.getInstance().invalidate(item.getX(), item.getY());
            }
            else if (structure != null) {
                structure.nextMode();
                MainRenderer.getInstance().invalidate(structure.getX(), structure.getY());
            }
        }
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

        // Select character
        if (_interaction.isAction(UserInteraction.Action.NONE)) {
            CharacterModel c = _characters.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
            if (c != null && c != _selector.getSelectedCharacter()) {
                _selector.select(c);
            }
            else  {
                if (_selector.getSelectedCharacter() != null) {
                    _selector.getSelectedCharacter().setSelected(false);
                    _selector.clean();
                }

                int relX = getRelativePosX(x);
                int relY = getRelativePosY(y);

                AreaModel area = ((AreaManager)Game.getInstance().getManager(AreaManager.class)).getArea(relX, relY);
                ParcelModel parcel = Game.getWorldManager().getParcel(relX, relY);

                // Select resource
                if (parcel != null && parcel.getResource() != null) { _selector.select(parcel.getResource()); return true; }

                // Select item
                for (int x2 = 0; x2 < Constant.ITEM_MAX_WIDTH; x2++) {
                    for (int y2 = 0; y2 < Constant.ITEM_MAX_HEIGHT; y2++) {
                        ItemModel item = Game.getWorldManager().getItem(relX - x2, relY - y2);
                        if (item != null && item.getWidth() > x2 && item.getHeight() > y2) {
                            _selector.select(item);
                            return true;
                        }
                    }
                }

                // Select consumable
                if (_mode != Mode.INFO_CONSUMABLE && parcel != null && parcel.getConsumable() != null) { _selector.select(parcel.getConsumable()); return true; }

                // Select area
                if (_mode != Mode.INFO_AREA && area != null) { _selector.select(area, parcel); return true; }

                // Select structure
                if (_mode != Mode.INFO_STRUCTURE && parcel != null && parcel.getStructure() != null) { _selector.select(parcel.getStructure()); return true; }

                // Select parcel
                if (_mode != Mode.INFO_PARCEL && parcel != null) { _selector.select(parcel); return true; }
            }
        }

        return false;
    }

    public void onRightClick(int x, int y) {

        if (_interaction.isAction(UserInteraction.Action.SET_ROOM)) {
            _interaction.clean();
        }

        else if (_mode == Mode.ROOM && _interaction.getSelectedRoomType() == RoomModel.RoomType.NONE) {
//            final Room room = Game.getRoomManager().getRoom(getRelativePosX(x), getRelativePosY(y));
//            if (room != null) {
//                throw new RuntimeException("not implemented");
//                //_menu = new RoomContextualMenu(_app, 0, new Vector2f(x, y), new Vector2f(100, 120), _viewport, room);
//            } else {
//                _menu = null;
//            }
        }

        // Cancel selected items
        else {
            if (_mode == Mode.CHARACTER) {
                setMode(Mode.CREW);
                return;
            }
            _interaction.clean();
            toggleMode(Mode.NONE);
        }

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

    private void dumpRoomInfo(ParcelModel area) {
        if (area.getRoom() != null) {
//            for (ParcelModel a: area.getRoom().getParcels()) {
//                Log.info("in room: " + a.getX() + "x" + a.getY());
//            }
            Log.info("room size: " + area.getRoom().getParcels().size());
            Log.info("room exterior: " + area.getRoom().isExterior());
        }
    }

}
