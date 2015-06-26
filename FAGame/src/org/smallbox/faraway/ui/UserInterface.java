package org.smallbox.faraway.ui;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.engine.*;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.manager.CharacterManager;
import org.smallbox.faraway.game.manager.RelationManager;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.ToolTips.ToolTip;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.*;
import org.smallbox.faraway.game.model.room.RoomModel;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.UIMessage;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.panel.*;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

public class UserInterface implements GameEventListener {
    private static UserInterface		_self;
    private final LayoutFactory         _factory;
    private final ViewFactory           _viewFactory;
    private Viewport _viewport;
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
    private PanelConsole                _panelMessage;
    private ToolTip 					_selectedTooltip;
    private CharacterModel              _selectedCharacter;
    private ItemModel                   _selectedItem;
    private StructureModel              _selectedStructure;
    private ResourceModel               _selectedResource;
    private ParcelModel                 _selectedParcel;
    private RoomModel                   _selectedRoom;
    private AreaModel                   _selectedArea;
    private ItemInfo 					_selectedItemInfo;
    private ConsumableModel             _selectedConsumable;
    private int 						_update;
    private long                        _lastModified;
    private PanelInfoStructure          _panelInfoStructure;
    private PanelInfoItem               _panelInfoItem;
    private PanelInfoParcel             _panelInfoParcel;
    private PanelInfoResource           _panelInfoResource;
    private PanelInfoConsumable         _panelInfoConsumable;
    private PanelInfoArea               _panelInfoArea;

    private	BasePanel[]					_panels = new BasePanel[] {
            new PanelSystem(),
            new PanelTopInfo(),
            new PanelResources(),
            new PanelDev(),
            new PanelQuest(),
            new PanelCharacter(	    Mode.CHARACTER,         null),
            new PanelInfo(		    Mode.INFO, 		        null),
            new PanelInfoStructure(	Mode.INFO_STRUCTURE, 	null),
            new PanelInfoItem(	    Mode.INFO_ITEM, 	    null),
            new PanelInfoConsumable(Mode.INFO_CONSUMABLE,   null),
            new PanelInfoParcel(	Mode.INFO_PARCEL, 	    null),
            new PanelInfoArea(	    Mode.INFO_AREA, 	    null),
            new PanelInfoResource(	Mode.INFO_RESOURCE, 	null),
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
    public void onMouseEvent(GameTimer timer, Action action, MouseButton button, int x, int y, boolean rightPressed) {
        if (action == Action.MOVE) {
            onMouseMove(x, y, rightPressed);
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
        INFO_STRUCTURE, INFO_ITEM, INFO_PARCEL, INFO_RESOURCE, INFO_CONSUMABLE, AREA, INFO_AREA, MANAGER
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
        _characters = Game.getCharacterManager();
        _keyLeftPressed = false;
        _keyRightPressed = false;
        _cursor = new UserInterfaceCursor();

        for (BasePanel panel: _panels) {
            panel.init(_viewFactory, _factory, this, _interaction, SpriteManager.getInstance().createRenderEffect());
            _game.addObserver(panel);

            if (panel.getMode() != null) {
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
                    case INFO_PARCEL:
                        _panelInfoParcel = (PanelInfoParcel) panel;
                        break;
                    case INFO_RESOURCE:
                        _panelInfoResource = (PanelInfoResource) panel;
                        break;
                    case INFO_AREA:
                        _panelInfoArea = (PanelInfoArea) panel;
                        break;
                }
            }
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

    public int 				getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
    public int 				getRelativePosXMax(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMinScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosYMax(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMinScale() / Constant.TILE_HEIGHT); }
    public int 				getRelativePosXMin(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getMaxScale() / Constant.TILE_WIDTH); }
    public int 				getRelativePosYMin(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getMaxScale() / Constant.TILE_HEIGHT); }
    public ToolTip			getSelectedTooltip() { return _selectedTooltip; }
    public CharacterModel   getSelectedCharacter() { return _selectedCharacter; }
    public ParcelModel      getSelectedArea() { return _selectedParcel; }
    public ItemModel        getSelectedItem() { return _selectedItem; }
    public ResourceModel    getSelectedResource() { return _selectedResource; }
    public StructureModel   getSelectedStructure() { return _selectedStructure; }
    public ItemInfo			getSelectedItemInfo() { return _selectedItemInfo; }
    public RoomModel        getSelectedRoom() { return _selectedRoom; }
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
            clean();
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
        _panelMessage.refresh(update);

        // Refresh UI if needed by GameData (strings)
        if (GameData.getData().needUIRefresh) {
            GameData.getData().needUIRefresh = false;
            reload();
        }

        // Refresh UI if needed by UI files
        long lastResModified = Utils.getLastUIModified();
        if (update % 8 == 0 && lastResModified > _lastModified) {
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
                    || _interaction.isAction(UserInteraction.Action.SET_AREA)
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
                    _selectedCharacter = _characters.getNext(_selectedCharacter);
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

        // Set area
        if (_interaction.isAction(UserInteraction.Action.SET_AREA)) {
            ((AreaManager)Game.getInstance().getManager(AreaManager.class)).createArea(
                    _interaction.getSelectedAreaType(),
                    Math.min(_keyPressPosX, _keyMovePosX),
                    Math.min(_keyPressPosY, _keyMovePosY),
                    Math.max(_keyPressPosX, _keyMovePosX),
                    Math.max(_keyPressPosY, _keyMovePosY));
            return true;
        }

        // Set room
        if (_mode == Mode.ROOM) {
//            if (_keyPressPosX == _keyMovePosX && _keyPressPosY == _keyMovePosY) {
//                final Room room = Game.getRoomManager().getRoom(getRelativePosX(x), getRelativePosY(y));
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
            CharacterModel c = _characters.getCharacterAtPos(getRelativePosX(x), getRelativePosY(y));
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

                AreaModel area = ((AreaManager)Game.getInstance().getManager(AreaManager.class)).getArea(relX, relY);
                ParcelModel parcel = Game.getWorldManager().getParcel(relX, relY);

                // Select resource
                if (parcel != null && parcel.getResource() != null) { select(parcel.getResource()); return true; }

                // Select item
                for (int x2 = 0; x2 < Constant.ITEM_MAX_WIDTH; x2++) {
                    for (int y2 = 0; y2 < Constant.ITEM_MAX_HEIGHT; y2++) {
                        ItemModel item = Game.getWorldManager().getItem(relX - x2, relY - y2);
                        if (item != null && item.getWidth() > x2 && item.getHeight() > y2) {
                            select(item);
                            return true;
                        }
                    }
                }

                // Select consumable
                if (parcel != null && parcel.getConsumable() != null) { select(parcel.getConsumable()); return true; }

                // Select area
                if (area != null) { select(area); return true; }

                // Select structure
                if (parcel != null && parcel.getStructure() != null) { select(parcel.getStructure()); return true; }

                // Select parcel
                if (parcel != null) { select(parcel); return true; }
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

    public void select(RoomModel room) {
        clean();
        setMode(Mode.ROOM);
        _selectedRoom = room;
    }

    public void select(AreaModel area) {
        clean();
        setMode(Mode.INFO_AREA);
        _selectedArea = area;
        _panelInfoArea.select(area);
    }

    public void select(ResourceModel resource) {
        clean();
        setMode(Mode.INFO_RESOURCE);
        _selectedResource = resource;
        _panelInfoResource.select(resource);
    }

    public void select(ItemModel item) {
        clean();
        setMode(Mode.INFO);
        setMode(Mode.INFO_ITEM);
        _selectedItem = item;
        _panelInfoItem.select(item);
    }

    public void select(ConsumableModel consumable) {
        clean();
        setMode(Mode.INFO);
        setMode(Mode.INFO_CONSUMABLE);
        _selectedConsumable = consumable;
        _panelInfoConsumable.select(consumable);
    }

    public void select(StructureModel structure) {
        clean();
        setMode(Mode.INFO_STRUCTURE);
        _selectedStructure = structure;
        _panelInfoStructure.select(structure);

        dumpRoomInfo(structure.getParcel());
    }

    public void select(ParcelModel area) {
        clean();
        setMode(Mode.INFO_PARCEL);
        _selectedParcel = area;
        _panelInfoParcel.select(area);

        dumpRoomInfo(area);
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

    public void select(ItemInfo itemInfo) {
        clean();
        setMode(Mode.INFO_ITEM);
        _selectedItemInfo = itemInfo;
        _panelInfoItem.select(itemInfo);
    }

    public void clean() {
        _selectedParcel = null;
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

    public void select(MapObjectModel item) {
        if (item.isUserItem()) {
            select((ItemModel)item);
        }
        else if (item.isStructure()) {
            select((StructureModel)item);
        }
    }
}
