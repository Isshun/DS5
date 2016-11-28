package org.smallbox.faraway.core.game.module.ui;

import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.renderer.BaseRenderer;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.engine.renderer.Viewport;
import org.smallbox.faraway.client.ui.ApplicationClient;

/**
 * Created by Alex on 24/11/2016.
 */
public class UIRenderer extends BaseRenderer {

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress) {
        ApplicationClient.uiManager.draw(renderer, Application.gameManager.isLoaded());
    }

    @Override
    public int getLevel() {
        return 1;
    }
}
