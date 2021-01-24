package org.smallbox.faraway.client.render;

import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class GDXRenderer extends GDXRendererBase {
    @Inject protected WorldCameraManager worldCameraManager;

    public void init() {
        super.init();
    }

    public void refresh() {
        worldCameraManager.update();
        _batch.setProjectionMatrix(worldCameraManager.getCombinedProjection());
    }

    @Override
    protected Matrix4 getCombinedProjection() {
        return worldCameraManager.getCombinedProjection();
    }

    @Override
    protected float getZoom() {
        return worldCameraManager.getZoom();
    }

    public void draw(View view, int x, int y) {
        view.draw(this, x, y);
    }

}