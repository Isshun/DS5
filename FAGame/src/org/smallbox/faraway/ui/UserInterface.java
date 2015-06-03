package org.smallbox.faraway.ui;

import org.smallbox.faraway.*;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.engine.ui.UIEventManager;
import org.smallbox.faraway.engine.ui.UIMessage;
import org.smallbox.faraway.engine.ui.ViewFactory;
import org.smallbox.faraway.engine.util.Constant;
import org.smallbox.faraway.manager.CharacterManager;
import org.smallbox.faraway.manager.ServiceManager;
import org.smallbox.faraway.manager.SpriteManager;
import org.smallbox.faraway.manager.Utils;
import org.smallbox.faraway.model.ToolTips.ToolTip;
import org.smallbox.faraway.model.character.CharacterModel;
import org.smallbox.faraway.model.item.*;
import org.smallbox.faraway.model.room.Room;
import org.smallbox.faraway.ui.panel.*;

public class UserInterface implements GameEventListener {
    private static UserInterface		_self;
    private final LayoutFactory         _factory;
    private final ViewFactory           _viewFactory;
    private Viewport                    _viewport;
    private boolean						_keyLeftPressed;
    private boolean						_keyRightPressed;
    private int							_mouseRightPressX;
    private int							_mouseRightPressY;
    private int							_keyPressPosX;
    private int							_keyPressPosY;
    private int							_keyMovePosX;
    private int							_keyMovePosY;
    private UserInteraction				_interaction;
    private CharacterManager        	_characteres;
    private UIMessage 					_message;
    private Mode 						_mode;
    private ContextualMenu 				_menu;
    private Game 						_game;
    private boolean 					_mouseOnMap;
    private BasePanel 					_currentPanel;
    private UserInterfaceCursor			_cursor;
    private long 						_lastLeftClick;
    private int 						_lastInput;
    private PanelConsole                _panelMessage;
    private ToolTip 					_selectedTooltip;
    private CharacterModel _selectedCharacter;
    private UserItem 					_selectedItem;
    private StructureItem 				_selectedStructure;
    private WorldResource				_selectedResource;
    private WorldArea 					_selectedArea;
    private Room 						_selectedRoom;
    private ItemInfo 					_selectedItemInfo;
    private ConsumableItem              _selectedConsumable;
    private int 						_update;
    private long                        _lastModified;
    private PanelInfoStructure          _panelInfoStructure;
    private PanelInfoItem               _panelInfoItem;
    private PanelInfoArea               _panelInfoArea;
    private PanelInfoResource           _panelInfoResource;
    private PanelInfoConsumable         _panelInfoConsumable;

    private	BasePanel[]					_panels = new BasePanel[] {
            new PanelSystem(),
            new PanelResources(),
            new PanelCharacter(	    Mode.CHARACTER,         null),
            new PanelInfo(		    Mode.INFO, 		        null),
            new PanelInfoStructure(	Mode.INFO_STRUCTURE, 	null),
            new PanelInfoItem(	    Mode.INFO_ITEM, 	null),
            new PanelInfoConsumable(Mode.INFO_CONSUMABLE, 	null),
            new PanelInfoArea(	    Mode.INFO_AREA, 	null),
            new PanelInfoResource(	Mode.INFO_RESOURCE, 	null),
            new PanelDebug(		Mode.DEBUG, 	Key.TILDE),
            new PanelPlan(		Mode.PLAN, 		Key.P),
            new PanelRoom(		Mode.ROOM, 		Key.R),
            new PanelTooltip(	Mode.TOOLTIP, 	Key.F1),
            new PanelBuild(		Mode.BUILD, 	Key.B),
            new PanelScience(	Mode.SCIENCE, 	null),
            new PanelSecurity(	Mode.SECURITY, 	null),
            new PanelCrew(		Mode.CREW, 		Key.C),
            new PanelJobs(		Mode.JOBS, 		Key.O),
//			new PanelStats(		Mode.STATS, 	Key.S),
            new PanelManager(	Mode.MANAGER, 	Key.M),
            new PanelShortcut(	Mode.NONE, 		null),
    };

    @Override
    public void onKeyEvent(GameTimer timer, Action action, Key key, Modifier modifier) {
        if (action == Action.RELEASED) {
            if (checkKeyboard(key, _lastInput)) {
                return;
            }
        }
    }

    @Override
    public void onMouseEvent(GameTimer timer, Action action, MouseButton button, int x, int y) {
        if (action == Action.MOVE) {
            onMouseMove(x, y);
            UIEventManager.getInstance().onMouseMove(x, y);
        }

        for (BasePanel panel: _panels) {
            if (panel.onMouseEvent(timer, action, button, x, y)) {
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
                        else if (_lastLeftClick + 200 > timer.getElapsedTime()) {
                            onDoubleClick(x, y);
                        }
                        // Is simple click
                        else {
                            boolean use = onLeftClick(x, y);
                            if (use) {
                                onRefresh(_update);
                            }
                        }
                        _lastLeftClick = timer.getElapsedTime();
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
    public void onWindowEvent(GameTimer timer, Action action) {
    }

    public void reload() {
        for (BasePanel panel: _panels) {
            panel.clearAllViews();
            panel.init(_viewFactory, _factory, this, _interaction, null);
            panel.refresh(0);
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
        INFO_STRUCTURE, INFO_ITEM, INFO_AREA, INFO_RESOURCE, INFO_CONSUMABLE, MANAGER
    }

    public UserInterface(LayoutFactory layoutFactory, ViewFactory viewFactory) {
        _self = this;
        _factory = layoutFactory;
        _viewFactory = viewFactory;
        _interaction = new UserInteraction(this);
        _panelMessage = new PanelConsole();
        _panelMessage.init(viewFactory, layoutFactory, this, _interaction, null);
    }

    public void onCreate(Game game) {
        _game = game;
        _viewport = game.getViewport();
        _characteres = Game.getCharacterManager();
        _keyLeftPressed = false;
        _keyRightPressed = false;
        _cursor = new UserInterfaceCursor();

        for (BasePanel panel: _panels) {
            panel.init(_viewFactory, _factory, this, _interaction, SpriteManager.getInstance().createRenderEffect());

            switch (panel.getMode()) {
                case INFO_STRUCTURE:
                    _panelInfoStructure = (PanelInfoStructure) panel;
                    break;
                case INFO_ITEM:
                    _panelInfoItem = (PanelInfoItem) panel;
                    break;
                case INFO_CONSUMABLE:
                    _panelInfoConsumable = (PanelInfoConsumable) panel;
                    break;
                case INFO_AREA:
                    _panelInfoArea = (PanelInfoArea) panel;
                    break;
                case INFO_RESOURCE:
                    _panelInfoResource = (PanelInfoResource) panel;
                    break;
            }
        }

        setMode(Mode.NONE);
    }

    public void	onMouseMove(int x, int y) {
        _keyMovePosX = getRelativePosX(x);
        _keyMovePosY = getRelativePosY(y);

        // TODO
        _mouseOnMap = x < 1500;

        // right button pressed
        if (_keyRightPressed) {
            _viewport.update(x, y);
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
        _mouseRightPressX = x;
        _mouseRightPressY = y;
        _viewport.startMove(x, y);
    }

    public int 				getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
    public int 				getRelativePosXMax(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosYMax(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
    public int 				getRelativePosXMin(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosYMin(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }
    public ToolTip			getSelectedTooltip() { return _selectedTooltip; }
    public CharacterModel getSelectedCharacter() { return _selectedCharacter; }
    public WorldArea		getSelectedArea() { return _selectedArea; }
    public UserItem			getSelectedItem() { return _selectedItem; }
    public WorldResource	getSelectedResource() { return _selectedResource; }
    public StructureItem	getSelectedStructure() { return _selectedStructure; }
    public ItemInfo			getSelectedItemInfo() { return _selectedItemInfo; }
    public Room 			getSelectedRoom() { return _selectedRoom; }

    public int				getMouseX() { return _keyMovePosX; }
    public int				getMouseY() { return _keyMovePosY; }

    public void toogleMode(Mode mode) {
        setMode(_mode != mode ? mode : Mode.NONE);
    }

    public void setMode(Mode mode) {
        _interaction.clean();

        ((MainRenderer)MainRenderer.getInstance()).setMode(mode);

        _mode = mode;
        _menu = null;

        if (mode == Mode.NONE) {
            _interaction.clean();
            clean();
        }

        for (BasePanel panel: _panels) {
            if (_mode.equals(panel.getMode())) {
                _currentPanel = panel;
                panel.setVisible(true);
            } else if (panel.isAlwaysVisible() == false) {
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
        _panelMessage.refresh(update);

        long lastResModified = Utils.getLastUIModified();
        if (update % 8 == 0 && lastResModified > _lastModified) {
            SpriteManager.getInstance().loadStrings();
            _lastModified = lastResModified;
            reload();
        }
    }

    public void onDraw(GFXRenderer renderer, int update, long renderTime) {
        for (BasePanel panel: _panels) {
            panel.draw(renderer, null);
        }

        _panelMessage.draw(renderer, null);

        if (_mouseOnMap) {
            if (_interaction.isAction(UserInteraction.Action.SET_ROOM)
                    || _interaction.isAction(UserInteraction.Action.SET_PLAN)
                    || _interaction.isAction(UserInteraction.Action.BUILD_ITEM)) {
                if (_keyLeftPressed) {
                    _cursor.draw(renderer, _viewport.getRenderEffect(), Math.min(_keyPressPosX, _keyMovePosX),
                            Math.min(_keyPressPosY, _keyMovePosY),
                            Math.max(_keyPressPosX, _keyMovePosX),
                            Math.max(_keyPressPosY, _keyMovePosY));
                } else {
                    _cursor.draw(renderer, _viewport.getRenderEffect(), Math.min(_keyMovePosX, _keyMovePosX),
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
            if (panel.checkKey(key)) {
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
                if (_selectedCharacter != null) {
                    _selectedCharacter = _characteres.getNext(_selectedCharacter);
                }
                return true;

            case PAGEUP:
                ServiceManager.getWorldMap().upFloor();
                return true;

            case PAGEDOWN:
                ServiceManager.getWorldMap().downFloor();
                return true;

            case SPACE:
                _game.setRunning(!_game.isRunning());
                return true;

            default: break;
        }

        for (BasePanel panel: _panels) {
            if (key.equals(panel.getShortcut())) {
                toogleMode(panel.getMode());
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

        WorldArea area = ServiceManager.getWorldMap().getArea(getRelativePosX(x), getRelativePosY(y));
        if (area != null) {
            ItemBase item = area.getItem();
            ItemBase structure = area.getStructure();

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
        if (_keyLeftPressed == false) {
            return false;
        }
        _keyLeftPressed = false;

        // Set plan
        if (_interaction.isAction(UserInteraction.Action.SET_PLAN)) {
            _interaction.plan(
                    Math.min(_keyPressPosX, _keyMovePosX),
                    Math.min(_keyPressPosY, _keyMovePosY),
                    Math.max(_keyPressPosX, _keyMovePosX),
                    Math.max(_keyPressPosY, _keyMovePosY));
            return true;
        }

        // Set room
        if (_mode == Mode.ROOM) {
//            if (_keyPressPosX == _keyMovePosX && _keyPressPosY == _keyMovePosY) {
//                final Room room = Game.getRoomManager().get(getRelativePosX(x), getRelativePosY(y));
//                select(room);
//                return true;
//            }

            if (_interaction.isAction(UserInteraction.Action.SET_ROOM)) {
                _interaction.roomType(
                        _keyPressPosX, _keyPressPosY,
                        Math.min(_keyPressPosX, _keyMovePosX),
                        Math.min(_keyPressPosY, _keyMovePosY),
                        Math.max(_keyPressPosX, _keyMovePosX),
                        Math.max(_keyPressPosY, _keyMovePosY));
            }
            return true;
        }

        if (_interaction.hasAction()) {
            _interaction.action(
                    Math.min(_keyPressPosX, _keyMovePosX),
                    Math.min(_keyPressPosY, _keyMovePosY),
                    Math.max(_keyPressPosX, _keyMovePosX),
                    Math.max(_keyPressPosY, _keyMovePosY));
            return true;
        }

        // Click is catch by panel
        if (_currentPanel != null && _currentPanel.catchClick(x, y)) {
            return true;
        }

        // Select character
        if (_interaction.isAction(UserInteraction.Action.NONE)) {
            CharacterModel c = _characteres.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
            if (c != null && c != _selectedCharacter) {
                select(c);
            }
            else  {
                if (_selectedCharacter != null) {
                    _selectedCharacter.setSelected(false);
                    clean();
                }

                int relX = getRelativePosX(x);
                int relY = getRelativePosY(y);
                WorldArea a = ServiceManager.getWorldMap().getArea(relX, relY);
                if (a != null) {
                    if (a.getResource() != null) { select(a.getResource()); return true; }
                    else if (a.getItem() != null) { select(a.getItem()); return true; }
                    else if (a.getConsumable() != null) { select(a.getConsumable()); return true; }
                }
                for (int x2 = 0; x2 < Constant.ITEM_MAX_WIDTH; x2++) {
                    for (int y2 = 0; y2 < Constant.ITEM_MAX_HEIGHT; y2++) {
                        UserItem item = ServiceManager.getWorldMap().getItem(relX - x2, relY - y2);
                        if (item != null && item.getWidth() > x2 && item.getHeight() > y2) {
                            select(item);
                            return true;
                        }
                    }
                }
                if (a.getStructure() != null) { select(a.getStructure()); return true; }
                else { select(a); return true; }
            }
        }

        return false;
    }

    public void onRightClick(int x, int y) {

        // Move viewport
        if (_keyRightPressed && Math.abs(_mouseRightPressX - x) > 5 || Math.abs(_mouseRightPressY - y) > 5) {
            _viewport.update(x, y);
        }

        else if (_interaction.isAction(UserInteraction.Action.SET_ROOM)) {
            _interaction.clean();
        }

        else if (_mode == Mode.ROOM && _interaction.getSelectedRoomType() == Room.RoomType.NONE) {
//            final Room room = Game.getRoomManager().get(getRelativePosX(x), getRelativePosY(y));
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
            toogleMode(Mode.NONE);
        }

        _keyRightPressed = false;
    }

    public Mode getMode() {
        return _mode;
    }

    public void addMessage(int level, String message) {
        _panelMessage.addMessage(level, message);
    }

    public void back() {
        setMode(Mode.NONE);
    }

    public void select(ToolTip tooltip) {
        _interaction.clean();
        _selectedTooltip = tooltip;
        setMode(Mode.TOOLTIP);
    }

    public void select(CharacterModel character) {
        clean();
        setMode(Mode.CHARACTER);
        _selectedCharacter = character;
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(true);
        }
    }

    public void select(Room room) {
        clean();
        setMode(Mode.ROOM);
        _selectedRoom = room;
    }

    public void select(WorldResource resource) {
        clean();
        setMode(Mode.INFO_RESOURCE);
        _selectedResource = resource;
        _panelInfoResource.select(resource);
    }

    public void select(UserItem item) {
        clean();
        setMode(Mode.INFO);
        setMode(Mode.INFO_ITEM);
        _selectedItem = item;
        _panelInfoItem.select(item);
    }

    public void select(ConsumableItem consumable) {
        clean();
        setMode(Mode.INFO);
        setMode(Mode.INFO_CONSUMABLE);
        _selectedConsumable = consumable;
        _panelInfoConsumable.select(consumable);
    }

    public void select(StructureItem structure) {
        clean();
        setMode(Mode.INFO_STRUCTURE);
        _selectedStructure = structure;
        _panelInfoStructure.select(structure);

        dumpRoomInfo(structure.getArea());
    }

    public void select(WorldArea area) {
        clean();
        setMode(Mode.INFO_AREA);
        _selectedArea = area;
        _panelInfoArea.select(area);

        dumpRoomInfo(area);
    }

    private void dumpRoomInfo(WorldArea area) {
        if (area.getRoom() != null) {
            for (WorldArea a: area.getRoom().getAreas()) {
                System.out.println("in room: " + a.getX() + "x" + a.getY());
            }
            System.out.println("room size: " + area.getRoom().getAreas().size());
            System.out.println("room exterior: " + area.getRoom().isExterior());
        }
    }

    public void select(ItemInfo itemInfo) {
        clean();
        setMode(Mode.INFO_ITEM);
        _selectedItemInfo = itemInfo;
        _panelInfoItem.select(itemInfo);
    }

    public void clean() {
        _selectedArea = null;
        _selectedStructure = null;
        _selectedItemInfo = null;
        if (_selectedCharacter != null) {
            _selectedCharacter.setSelected(false);
        }
        _selectedCharacter = null;
        if (_selectedItem != null) {
            _selectedItem.setSelected(false);
        }
        _selectedItem = null;
        _selectedResource = null;
        _selectedTooltip = null;
    }

    public void select(ItemBase item) {
        if (item.isUserItem()) {
            select((UserItem)item);
        }
        else if (item.isStructure()) {
            select((StructureItem)item);
        }
    }
}
