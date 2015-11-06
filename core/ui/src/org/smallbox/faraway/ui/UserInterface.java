package org.smallbox.faraway.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.Viewport;
import org.smallbox.faraway.core.data.ItemInfo;
import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleHelper;
import org.smallbox.faraway.core.module.java.ModuleManager;
import org.smallbox.faraway.core.util.Constant;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.ui.engine.OnClickListener;
import org.smallbox.faraway.ui.engine.UIEventManager;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class UserInterface implements GameEventListener {
    private static class ContextEntry {
        public String                   label;
        public OnClickListener          listener;

        public ContextEntry(String label, OnClickListener listener) {
            this.label = label;
            this.listener = listener;
        }
    }

    private static UserInterface        _self;
    private Viewport                    _viewport;
    private boolean                     _keyLeftPressed;
    private boolean                     _keyRightPressed;
    private int                         _keyPressPosX;
    private int                         _keyPressPosY;
    private int                         _keyMovePosX;
    private int                         _keyMovePosY;
    private UserInteraction             _interaction;
    private Game                        _game;
    private boolean                     _mouseOnMap;
    private UICursor                    _cursor;
    private UISelection                 _selection;
    private long                        _lastLeftClick;
    private UserInterfaceSelector       _selector;
    private int                         _update;
    private UIFrame                     _context;
    public List<View>                   _views = new ArrayList<>();

    public static UserInterface getInstance() {
        if (_self == null) {
            _self = new UserInterface();
        }
        return _self;
    }

    public UserInterface() {
        _interaction = new UserInteraction();
        _selector = new UserInterfaceSelector(this);
        _selection = new UISelection();
        _context = new UIFrame();
        _context.setVisible(false);
    }

    public UserInterfaceSelector    getSelector() { return _selector; }
    public int                      getRelativePosX(int x) { return (int) ((x - _viewport.getPosX()) / _viewport.getScale() / Constant.TILE_WIDTH); }
    public int                      getRelativePosY(int y) { return (int) ((y - _viewport.getPosY()) / _viewport.getScale() / Constant.TILE_HEIGHT); }
    public int                      getMouseX() { return _keyMovePosX; }
    public int                      getMouseY() { return _keyMovePosY; }
    public UserInteraction          getInteraction() { return _interaction; }
    public void                     clearCursor() { _cursor = null; }
    public void                     setCursor(UICursor cursor) { _cursor = cursor; }
    public void                     setCursor(String cursorName) { _cursor = Data.getData().getCursor(cursorName); }
    public void                     setGame(Game game) { _game = game; _viewport = game.getViewport(); }

    // Used by lua modules
    public UILabel                  createLabel() { return new UILabel(); }
    public UIImage createImage() { return new UIImage(-1, -1); }
    public View                     createView() { return new UIFrame(-1, -1); }
    public UIGrid createGrid() { return new UIGrid(-1, -1); }
    public UIList                   createList() { return new UIList(-1, -1); }
    public void                     clearSelection() { _selector.clean(); }

    public void reload() {
        _views.clear();
        UIEventManager.getInstance().clear();
    }

    @Override
    public void onKeyEvent(Action action, Key key, Modifier modifier) {
        if (action == Action.RELEASED) {
            if (checkKeyboard(key)) {
                return;
            }
        }
    }

    @Override
    public void onMouseEvent(Action action, MouseButton button, int x, int y, boolean rightPressed) {
        for (GameModule module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module.onMouseEvent(action, button, x, y)) {
                return;
            }
        }

        if (action == Action.MOVE) {
            onMouseMove(x, y, rightPressed);
            UIEventManager.getInstance().onMouseMove(x, y);
            _selector.moveAt(getRelativePosX(x), getRelativePosY(y));
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
        }
    }

    @Override
    public void onWindowEvent(Action action) {
    }

    public void putDebug(ItemInfo itemInfo) {
        _interaction.set(UserInteraction.Action.PUT_ITEM_FREE, itemInfo);
        setCursor(Data.getData().getCursor("base.cursor.build"));
    }

    public void    onMouseMove(int x, int y, boolean rightPressed) {
        _keyMovePosX = getRelativePosX(x);
        _keyMovePosY = getRelativePosY(y);

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

    public void    onLeftPress(int x, int y) {
        if (UIEventManager.getInstance().has(x, y)) {
            return;
        }

        _keyLeftPressed = true;
        _keyMovePosX = _keyPressPosX = getRelativePosX(x);
        _keyMovePosY = _keyPressPosY = getRelativePosY(y);

        _selection.setStart(x, y);
    }

    public void    onRightPress(int x, int y) {
        if (UIEventManager.getInstance().has(x, y)) {
            return;
        }

        _keyRightPressed = true;
    }

    public void    onMouseWheel(int delta, int x, int y) {
        _viewport.setScale(delta, x, y);
    }

    public void onRefresh(int update) {
        _update = update;

        for (GameModule module: _game.getModules()) {
            if (module.isLoaded()) {
                module.refresh(update);
            }
        }

        Application.getInstance().notify(GameObserver::onRefreshUI);
    }

    public void draw(GDXRenderer renderer, boolean gameRunning) {
        _views.stream().filter(view -> view.isVisible() && (gameRunning || !view.inGame()) && (view.getModule() == null || view.getModule().isLoaded())).forEach(view -> view.draw(renderer, 0, 0));

        if (_context.isVisible()) {
            _context.draw(renderer, 0, 0);
        }

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

//        if (_menu != null) {
//            _menu.draw(renderer, 0, 0);
//        }
    }

    public boolean checkKeyboard(Key key) {
        for (GameModule module: ModuleManager.getInstance().getModules()) {
            if (module.isLoaded() && module.onKey(key)) {
                return true;
            }
        }

        return false;
    }

    public void onDoubleClick(int x, int y) {
    }

    public boolean onLeftClick(int x, int y) {
        if (!_keyLeftPressed) {
            return false;
        }
        _keyLeftPressed = false;
        _selection.clear();

        if (_context.isVisible()) {
            _context.setVisible(false);
            return true;
        }

        // Check user actions
        if (_interaction.onKeyLeft(_keyPressPosX, _keyPressPosY,
                Math.min(_keyPressPosX, _keyMovePosX),
                Math.min(_keyPressPosY, _keyMovePosY),
                Math.max(_keyPressPosX, _keyMovePosX),
                Math.max(_keyPressPosY, _keyMovePosY))) {
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

        // Select characters
        if (_interaction.isAction(UserInteraction.Action.NONE)) {
            if (_selector.selectAt(getRelativePosX(x), getRelativePosY(y))) {
                return true;
            }
        }

        return false;
    }

    public void onRightClick(int x, int y) {
        _keyRightPressed = false;

        final CharacterModel character = _selector.getSelectedCharacter();
        final ParcelModel parcel = ModuleHelper.getWorldModule().getParcel(getRelativePosX(x), getRelativePosY(y));

        if (character != null) {
            if (character.getParcel() == parcel) {
                openContextMenu(new ContextEntry[] {
                        new ContextEntry("Wake up", view -> character.getNeeds().setSleeping(false)),
                }, x, y);
                return;
            } else if (parcel != null && (parcel.getItem() != null || parcel.getConsumable() != null || parcel.getResource() != null || (parcel.getStructure() != null && !parcel.getStructure().isFloor()))) {
                openContextMenu(new ContextEntry[] {
                        new ContextEntry("Use", view -> character.getNeeds().setSleeping(false)),
                        new ContextEntry("Dump", view -> character.getNeeds().setSleeping(false)),
                        new ContextEntry("Clean", view -> character.getNeeds().setSleeping(false)),
                        new ContextEntry("Dump", view -> character.getNeeds().setSleeping(false)),
                }, x, y);
                return;
            } else {
                _selector.getSelectedCharacter().moveTo(WorldHelper.getParcel(getRelativePosX(x), getRelativePosY(y)), null);
                return;
            }
        }

        // TODO: clean
        Application.getInstance().notify(observer -> observer.onKeyPress(Key.ESCAPE));
    }

    private void openContextMenu(ContextEntry[] entries, int x, int y) {
        _context.setVisible(true);
        _context.removeAllViews();
        _context.setPosition(x + 16, y + 16);
        _context.setBackgroundColor(Color.BLUE);

        int index = 0;
        for (ContextEntry entry: entries) {
            UILabel lbEntry = new UILabel(100, 20);
            lbEntry.setTextSize(14);
            lbEntry.setText(entry.label);
            lbEntry.setOnClickListener(entry.listener);
            lbEntry.setTextAlign(View.Align.CENTER_VERTICAL);
            lbEntry.setPosition(4, index++ * 20);
            _context.addView(lbEntry);
        }

        _context.setSize(100, index * 20);
    }

    public View findById(String id) {
        int resId = id.hashCode();
        for (View view: _views) {
            if (view.getId() == resId) {
                return view;
            }
            View v = view.findById(resId);
            if (v != null) {
                return v;
            }
        }
        return null;
    }
}