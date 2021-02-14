package org.smallbox.faraway.client.input;

import com.badlogic.gdx.InputProcessor;
import org.apache.commons.collections4.CollectionUtils;
import org.smallbox.faraway.client.debug.DebugService;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameContextMenu.GameContextMenuManager;
import org.smallbox.faraway.client.renderer.MapRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.renderer.WorldCameraManager;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;

import java.util.function.BiConsumer;

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
    @Inject private CameraMoveInputManager cameraMoveInputManager;
    @Inject private GameManager gameManager;
    @Inject private ShortcutManager shortcutManager;
    @Inject private Viewport viewport;
    @Inject private MapRenderer mapRenderer;
    @Inject private WorldCameraManager worldCameraManager;

    private KeyModifier _Key_modifier = KeyModifier.NONE;
    private int _lastPosX;
    private int _lastPosY;
    private int _touchDownX;
    private int _touchDownY;
    private int _touchDragX;
    private int _touchDragY;
    private int _touchButton;
    private boolean _touchDrag;
    private BiConsumer<Integer, KeyModifier> nextKeyConsumer;

    @Override
    public boolean keyDown(int keycode) {
        if (debugService.isDebugMode()) {
            return false;
        }

        if (keycode == Keys.CONTROL_LEFT) {
            _Key_modifier = KeyModifier.CONTROL;
        }

        if (keycode == Keys.ALT_LEFT) {
            _Key_modifier = KeyModifier.ALT;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            _Key_modifier = KeyModifier.SHIFT;
        }

        if (cameraMoveInputManager.keyDown(keycode)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {

        if (nextKeyConsumer != null) {
            nextKeyConsumer.accept(keycode, _Key_modifier);
        }

        if (debugService.isDebugMode()) {
            debugService.keyUp(keycode);
            return false;
        }

        if (cameraMoveInputManager.keyUp(keycode)) {
            return false;
        }

        if (keycode == Keys.CONTROL_LEFT) {
            _Key_modifier = KeyModifier.NONE;
        }

        if (keycode == Keys.ALT_LEFT) {
            _Key_modifier = KeyModifier.NONE;
        }

        if (keycode == Keys.SHIFT_LEFT) {
            _Key_modifier = KeyModifier.NONE;
        }

        // Clear UiEventManager selection listener when escape key is pushed
        if (keycode == Keys.ESCAPE && gameSelectionManager.getSelectionListener() != null) {
            gameSelectionManager.setSelectionListener(null);
            return false;
        }

        if (uiManager.onKeyEvent(MouseAction.RELEASED, keycode, _Key_modifier)) {
            return false;
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

    public void getNextKey(BiConsumer<Integer, KeyModifier> consumer) {
        nextKeyConsumer = consumer;
    }
}