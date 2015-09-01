package org.smallbox.faraway.ui;

import org.smallbox.faraway.Application;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.GDXRenderer;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.model.item.ParcelModel;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.ModuleManager;
import org.smallbox.faraway.game.module.character.CharacterModule;
import org.smallbox.faraway.ui.cursor.BuildCursor;
import org.smallbox.faraway.ui.engine.LayoutFactory;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.ViewFactory;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;
import org.smallbox.faraway.ui.engine.view.View;
import org.smallbox.faraway.ui.panel.*;
import org.smallbox.faraway.ui.panel.debug.OxygenManagerPanel;
import org.smallbox.faraway.ui.panel.debug.ParcelDebugPanel;
import org.smallbox.faraway.ui.panel.right.*;
import org.smallbox.faraway.util.Constant;
import org.smallbox.faraway.util.Log;
import org.smallbox.faraway.util.Utils;

public class UserInterface implements GameEventListener {
    private int _keyPressX;
    private int _keyPressY;

    public UserInteraction getInteraction() {
        return _interaction;
    }

    private static class ContextEntry {
        public String                   label;
        public OnClickListener          listener;

        public ContextEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

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
    private CharacterModule             _characters;
    private Mode 						_mode;
    private ContextualMenu 				_menu;
    private Game 						_game;
    private boolean 					_mouseOnMap;
    private BasePanel 					_currentPanel;
    private UICursor                    _cursor;
    private UISelection                 _selection;
    private long 						_lastLeftClick;
    private int 						_lastInput;
    private PanelConsole                _panelConsole;
    private UserInterfaceSelector       _selector;
    private int 						_update;
    private long                        _lastModified;
    private FrameLayout                 _context;

    private	BasePanel[]					_panels = new BasePanel[] {
            new PanelSystem(),
            new PanelResources(),

//            new PanelQuest(),
//            new PanelCharacter(	    Mode.CHARACTER,         null),
//            new PanelInfo(		    Mode.INFO, 		        null),
//            new PanelInfoStructure(	Mode.INFO_STRUCTURE, 	null),
//            new PanelInfoItem(	    Mode.INFO_ITEM, 	    null),
//            new PanelInfoConsumable(Mode.INFO_CONSUMABLE,   null),
//            new PanelInfoParcel(	Mode.INFO_PARCEL, 	    null),
//            new PanelInfoArea(	    Mode.INFO_AREA, 	    null),
//            new PanelInfoAnimal(	Mode.INFO_ANIMAL, 	    null),
//            new PanelPlanModule(		    Mode.PLAN, 		        Key.P),
//            new PanelRoom(		    Mode.ROOM, 		        Key.R),
            new PanelTooltip(	    Mode.TOOLTIP, 	        Key.F1),
            new PanelBuild(		    Mode.BUILD, 	        Key.B),
            new PanelScience(	    Mode.SCIENCE, 	        null),
//            new PanelCrew(		    Mode.CREW, 		        Key.C),
//            new PanelJobs(		    Mode.JOBS, 		        Key.O),
            new PanelArea(		    Mode.AREA, 		        Key.A),
//			new PanelStats(		    Mode.STATS, 	        Key.S),
            new PanelManager(	    Mode.MANAGER, 	        Key.M),
//            new PanelShortcut(	    Mode.NONE, 		        null),
            new PanelPlanet(),
            new PanelTopInfo(),
//            new PanelTopRight(),

            // Debug
//            new TemperatureManagerPanel(),
            new OxygenManagerPanel(),
            new JobDebugPanel(),
            new ParcelDebugPanel(),
    };

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
        _selection = new UISelection();
        _panelConsole = new PanelConsole();
        _panelConsole.init(viewFactory, layoutFactory, this, _interaction);
        _context = ViewFactory.getInstance().createFrameLayout();
        _context.setVisible(false);
    }

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
        for (BasePanel panel: _panels) {
            if (panel.isVisible() && panel.onMouseEvent(action, button, x, y)) {
                return;
            }
        }

        for (GameModule module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module.onMouseEvent(action, button, x, y)) {
                return;
            }
        }

        if (action == Action.MOVE) {
            if (_currentPanel != null) {
                _currentPanel.onMouseMove(x, y);
            }
            onMouseMove(x, y, rightPressed);
            UIEventManager.getInstance().onMouseMove(x, y);
            return;
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
            panel.init(_viewFactory, _factory, this, _interaction);
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

    public void setCursor(UICursor cursor) {
        _cursor = cursor;
    }

    public void onCreate(Game game) {
        _game = game;
        _viewport = game.getViewport();
        _characters = Game.getCharacterManager();
        _keyLeftPressed = false;
        _keyRightPressed = false;

        for (BasePanel panel: _panels) {
            panel.init(_viewFactory, _factory, this, _interaction);
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

        if (_keyLeftPressed) {
            _selection.setPosition(x, y);
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
        _keyPressX = x;
        _keyPressY = y;
        _keyMovePosX = _keyPressPosX = getRelativePosX(x);
        _keyMovePosY = _keyPressPosY = getRelativePosY(y);

        _selection.setStart(x, y);
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
        for (GameModule module: _game.getModules()) {
            if (module.isLoaded()) {
                module.refresh(update);
            }
        }
        _panelConsole.refresh(update);
    }

    public void onDraw(GDXRenderer renderer, int update, long renderTime) {

        for (BasePanel panel: _panels) {
            panel.draw(renderer, null);
        }

        for (GameModule module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module instanceof GameUIModule) {
                ((GameUIModule)module).draw(renderer);
            }
        }

        if (_context.isVisible()) {
            _context.draw(renderer, null);
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

        renderer.draw(_selection, 100, 100);

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

        for (GameModule module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module.onKey(key)) {
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

        if (_context.isVisible()) {
            _context.setVisible(false);
            return true;
        }

        // Check selection
        if (_selector.selectAt(
                getRelativePosX(_selection.getFromX()),
                getRelativePosY(_selection.getFromY()),
                getRelativePosX(_selection.getToX()),
                getRelativePosY(_selection.getToY()))) {
            _selection.clear();
            return true;
        }
        _selection.clear();

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
        final CharacterModel character = _selector.getSelectedCharacter();
        final ParcelModel parcel = Game.getWorldManager().getParcel(getRelativePosX(x), getRelativePosY(y));

        if (character != null) {
            if (character.getParcel() == parcel) {
                openContextMenu(new ContextEntry[] {
                        new ContextEntry("Wake up", view -> character.getNeeds().setSleeping(false)),
                }, x, y);
            } else if (parcel.getItem() != null || parcel.getConsumable() != null || parcel.getResource() != null || (parcel.getStructure() != null && !parcel.getStructure().isFloor())) {
                openContextMenu(new ContextEntry[] {
                        new ContextEntry("Use", view -> character.getNeeds().setSleeping(false)),
                        new ContextEntry("Dump", view -> character.getNeeds().setSleeping(false)),
                        new ContextEntry("Clean", view -> character.getNeeds().setSleeping(false)),
                        new ContextEntry("Dump", view -> character.getNeeds().setSleeping(false)),
                }, x, y);
            } else {
                _selector.getSelectedCharacter().moveTo(null, getRelativePosX(x), getRelativePosY(y), null);
            }
        }

//        _interaction.clean();
//        _cursor = null;
//        toggleMode(Mode.NONE);

        _keyRightPressed = false;
    }

    private void openContextMenu(ContextEntry[] entries, int x, int y) {
        _context.setVisible(true);
        _context.removeAllViews();
        _context.setPosition(x + 16, y + 16);
        _context.setBackgroundColor(Color.BLUE);

        int index = 0;
        for (ContextEntry entry: entries) {
            UILabel lbEntry = ViewFactory.getInstance().createTextView(100, 20);
            lbEntry.setTextSize(14);
            lbEntry.setText(entry.label);
            lbEntry.setOnClickListener(entry.listener);
            lbEntry.setTextAlign(View.Align.CENTER_VERTICAL);
            lbEntry.setPosition(4, index++ * 20);
            _context.addView(lbEntry);
        }

        _context.setSize(100, index * 20);
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
