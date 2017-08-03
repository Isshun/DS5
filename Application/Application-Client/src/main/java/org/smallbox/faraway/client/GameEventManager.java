package org.smallbox.faraway.client;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.core.dependencyInjector.ApplicationObject;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Collection;

/**
 * Created by Alex on 16/07/2017.
 */
@ApplicationObject
public class GameEventManager implements EventManager {

    private boolean _mousePressed;

    // Current mouse position
    protected int _mouseX;
    protected int _mouseY;

    // Mouse press position
    protected int _mouseDownX;
    protected int _mouseDownY;

    // Mouse release position
    protected int _mouseUpX;
    protected int _mouseUpY;

    public boolean isMousePressed() {
        return _mousePressed;
    }

    public int getMouseX() {
        return _mouseX;
    }

    public int getMouseY() {
        return _mouseY;
    }

    public int getMouseDownX() {
        return _mouseDownX;
    }

    public int getMouseDownY() {
        return _mouseDownY;
    }

    public int getMouseUpX() {
        return _mouseUpX;
    }

    public int getMouseUpY() {
        return _mouseUpY;
    }

    @Override
    public boolean onMousePress(int x, int y, int button) {
        _mouseDownX = x;
        _mouseDownY = y;
        _mouseX = x;
        _mouseY = y;
        _mousePressed = true;
        return false;
    }

    @Override
    public boolean onMouseRelease(int x, int y, int button) {
        _mouseUpX = x;
        _mouseUpY = y;
        _mouseX = x;
        _mouseY = y;
        _mousePressed = false;

        Collection<? extends ObjectModel> selected = ApplicationClient.selectionManager.getSelected();

        // Move character on pointer position
        if (button == Input.Buttons.RIGHT) {
            if (selected != null) {
                for (ObjectModel object: selected) {
                    if (object instanceof CharacterModel) {
                        ((CharacterModel)object).moveTo(WorldHelper.getParcel(
                                ApplicationClient.layerManager.getViewport().getWorldPosX(x),
                                ApplicationClient.layerManager.getViewport().getWorldPosY(y),
                                ApplicationClient.layerManager.getViewport().getFloor()
                        ));
                        return true;
                    }
                }
            }
        }

        ApplicationClient.notify(obs -> obs.onMouseRelease(x, y, button));
        return false;
    }

    @Override
    public boolean onMouseMove(int x, int y, boolean pressed) {
        _mouseX = x;
        _mouseY = y;
        return false;
    }

}
