package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.client.renderer.GDXRenderer;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.GameRenderer;

/**
 * Created by Alex on 24/11/2016.
 */
@GameRenderer(level = 1, visible = true)
public class UIRenderer extends BaseRenderer {

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        ApplicationClient.uiManager.draw(renderer, Application.gameManager.isLoaded());
    }
}
