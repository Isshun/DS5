package org.smallbox.faraway.client.input;

import com.badlogic.gdx.InputProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.debug.DebugService;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameContextMenu.GameContextMenuManager;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.renderer.WorldCameraManager;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.event.GameEvent;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.Parcel;

import static com.badlogic.gdx.Input.Buttons;
import static com.badlogic.gdx.Input.Keys;

@ApplicationObject
public class InputManager implements InputProcessor {
    @Inject private DebugService debugService;
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private GameEventManager gameEventManager;
    @Inject private GameContextMenuManager gameContextMenuManager;
    @Inject private GameActionManager gameActionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private UIManager uiManager;
    @Inject private WorldInputManager worldInputManager;
    @Inject private GameManager gameManager;
    @Inject private ShortcutManager shortcutManager;
    @Inject private Viewport viewport;
    @Inject private MapRenderer mapRenderer;
    @Inject private WorldCameraManager worldCameraManager;

    private GameEventListener.Modifier _modifier = GameEventListener.Modifier.NONE;
    private int _lastPosX;
    private int _lastPosY;
    private int _touchDownX;
    private int _touchDownY;
    private int _touchDragX;
    private int _touchDragY;
    private int _touchButton;
    private boolean _touchDrag;

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.GRAVE) {
            return false;
        }

        if (debugService.isDebugMode()) {
            return false;
        }

        if (keycode == Keys.CONTROL_LEFT) {
            _modifier = GameEventListener.Modifier.CONTROL;
        }

        if (keycode == Keys.ALT_LEFT) {
            _modifier = GameEventListener.Modifier.ALT;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            _modifier = GameEventListener.Modifier.SHIFT;
        }

        worldInputManager.keyDown(keycode);

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        if (keycode == Keys.GRAVE) {
            debugService.toggleDebugMode();
            return false;
        }

        if (debugService.isDebugMode()) {
            debugService.keyUp(keycode);
            return false;
        }

        worldInputManager.keyUp(keycode);

        if (keycode == Keys.CONTROL_LEFT) {
            _modifier = GameEventListener.Modifier.NONE;
        }

        if (keycode == Keys.ALT_LEFT) {
            _modifier = GameEventListener.Modifier.NONE;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            _modifier = GameEventListener.Modifier.NONE;
        }

        // Clear UiEventManager selection listener when escape key is pushed
        if (keycode == Keys.ESCAPE && gameSelectionManager.getSelectionListener() != null) {
            gameSelectionManager.setSelectionListener(null);
            return false;
        }

        if (uiManager.onKeyEvent(GameEventListener.Action.RELEASED, keycode, _modifier)) {
            return false;
        }

        if (gameManager.isLoaded()) {
            GameEvent event = new GameEvent(keycode);
            Application.notifyClient(observer -> observer.onKeyPressWithEvent(event, keycode));
            Application.notifyClient(observer -> observer.onKeyEvent(GameEventListener.Action.RELEASED, keycode, _modifier));
        }

        // TODO: A deplacer dans ApplicationShortcutManage
        // Call shortcut strategy
        shortcutManager.action(keycode);

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (debugService.isDebugMode()) {
            debugService.typeCharacter(character);
            return true;
        }

        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        _touchDrag = false;
        _touchButton = button;
        _touchDragX = _touchDownX = x;
        _touchDragY = _touchDownY = y;

        if (button == Buttons.LEFT) {

            if (debugService.isDebugMode()) {
                return false;
            }

            if (gameContextMenuManager.getMenu() != null) {
                return true;
            }

            // Passe l'evenement à l'ui event manager
            if (uiEventManager.onMousePress(x, y, button)) {
                return false;
            }

            // Passe l'evenement au game event manager
            if (gameEventManager.onMousePress(x, y, button)) {
                return false;
            }

        }

        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        Parcel parcel = WorldHelper.getParcel(viewport.getWorldPosX(x), viewport.getWorldPosY(y), viewport.getFloor());

        if (debugService.isDebugMode()) {
            debugService.click(x, y);
            return false;
        }

        else if (!moveMoved(x, y) && gameContextMenuManager.hasContent(parcel) && !gameActionManager.hasAction() && button == Buttons.RIGHT) {
            gameContextMenuManager.open(parcel, x, y);
            return true;
        }

        else if (!moveMoved(x, y) && gameContextMenuManager.getMenu() != null && button == Buttons.LEFT) {
            gameContextMenuManager.click(x, y);
            return true;
        }

        else if (!moveMoved(x, y) && CollectionUtils.isNotEmpty(gameSelectionManager.getSelected()) && button == Buttons.RIGHT) {
            gameSelectionManager.clear();
            return true;
        }

        // Passe l'evenement à l'ui event manager
        else if (uiEventManager.onMouseRelease(x, y, button)) {
            return false;
        }

        // Passe l'evenement au game event manager
        else if (gameEventManager.onMouseRelease(x, y, button)) {
            return false;
        }

        return false;
    }

    private boolean moveMoved(int x, int y) {
        return Math.abs(_touchDownX - x) + Math.abs(_touchDownY - y) > 5;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {

        if (_touchButton == Buttons.RIGHT) {
            viewport.move((x - _touchDragX) * 3, (y - _touchDragY) * 3);
        }

        // Passe l'evenement au game event manager
        if (gameEventManager.onMouseMove(x, y, true)) {
            return false;
        }

        _touchDragX = x;
        _touchDragY = y;
        _touchDrag = true;

        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        _lastPosX = x;
        _lastPosY = y;

        // Passe l'evenement à l'ui event manager
        if (uiEventManager.onMouseMove(x, y, false)) {
            return false;
        }

        // Passe l'evenement au game event manager
        if (gameEventManager.onMouseMove(x, y, false)) {
            return false;
        }

        Application.notifyClient(observer -> observer.onMouseMove(x, y, -1));

        return false;
    }


    @Override
    public boolean scrolled(float amountX, float amountY) {

        if (amountY < 0) {
            worldCameraManager.zoomIn();

//            // Passe l'evenement à l'ui manager
//            if (uiManager.onMouseEvent(GameEventListener.Action.RELEASED, Buttons.FORWARD, _lastPosX, _lastPosY, false)) {
//                return true;
//            }


            return true;
        }

        if (amountY > 0) {
            worldCameraManager.zoomOut();

//            // Passe l'evenement à l'ui manager
//            if (uiManager.onMouseEvent(GameEventListener.Action.RELEASED, Buttons.BACK, _lastPosX, _lastPosY, false)) {
//                return true;
//            }

            return true;
        }

        return false;
    }

    public int getMouseX() {
        return _lastPosX;
    }

    public int getMouseY() {
        return _lastPosY;
    }

    public int getTouchDownX() {
        return _touchDownX;
    }

    public int getTouchDownY() {
        return _touchDownY;
    }

    public int getTouchDragX() {
        return _touchDragX;
    }

    public int getTouchDragY() {
        return _touchDragY;
    }

}