package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.math.Matrix4;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class MapRenderer extends BaseRenderer {
    @Inject protected WorldCameraManager worldCameraManager;

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