package org.smallbox.faraway.client;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.core.game.helper.WorldHelper;
import org.smallbox.faraway.core.game.model.ObjectModel;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;

import java.util.Collection;

/**
 * Created by Alex on 16/07/2017.
 */
public class GameEventManager implements EventManager {

    @Override
    public boolean onMousePress(int x, int y, int button) {
        return false;
    }

    @Override
    public boolean onMouseRelease(int x, int y, int button) {

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
    public boolean onMouseMove(int x, int y) {
        return false;
    }

}
