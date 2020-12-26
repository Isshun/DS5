package org.smallbox.faraway.client;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.render.LayerManager;
import org.smallbox.faraway.client.selection.SelectionManager;
import org.smallbox.faraway.common.ObjectModel;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

import java.util.Collection;

@ApplicationObject
public class GameEventManager implements EventManager {

    @Inject
    private LayerManager layerManager;

    @Inject
    private SelectionManager selectionManager;

    @Inject
    private GameActionManager gameActionManager;

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

        Collection<? extends ObjectModel> selected = selectionManager.getSelected();

//        // Move character on pointer position
//        if (button == Input.Buttons.RIGHT) {
//            if (selected != null) {
//                for (ObjectModel object: selected) {
//                    if (object instanceof CharacterModel) {
//                        ((CharacterModel)object).moveTo(WorldHelper.getParcel(
//                                layerManager.getViewport().getWorldPosX(x),
//                                layerManager.getViewport().getWorldPosY(y),
//                                layerManager.getViewport().getFloor()
//                        ));
//                        return true;
//                    }
//                }
//            }
//        }

        if (button == Input.Buttons.RIGHT) {
            gameActionManager.clearAction();
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
