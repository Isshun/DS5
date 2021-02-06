package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;

public abstract class BaseMapLayer extends BaseLayer {
    @Inject private InputManager inputManager;
    @Inject private Viewport viewport;

    protected boolean hasCursorOver(Parcel parcel) {
        return parcel == WorldHelper.getParcel(viewport.getWorldPosX(inputManager.getMouseX()), viewport.getWorldPosY(inputManager.getMouseY()), viewport.getFloor());
    }

}
