package org.smallbox.faraway.client.layer;

import org.smallbox.faraway.client.input.InputManager;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.world.WorldHelper;
import org.smallbox.faraway.game.world.WorldModule;

public abstract class BaseMapLayer extends BaseLayer {
    @Inject private ApplicationConfig applicationConfig;
    @Inject private InputManager inputManager;
    @Inject private WorldModule worldModule;
    @Inject private Viewport viewport;

    protected boolean hasCursorOver(Parcel parcel) {
        return parcel == WorldHelper.getParcel(viewport.getWorldPosX(inputManager.getMouseX()), viewport.getWorldPosY(inputManager.getMouseY()), viewport.getFloor());
    }
}
