package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;

@ApplicationObject
public class GDXRendererUI extends GDXRendererBase {

    public void init() {
        super.init();

        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.zoom = 1f;
    }

    public void refresh() {
        _camera.update();
    }

    public void draw(View view, int x, int y) {
        view.draw(this, x, y);
    }

    protected int mapToScreenX(int mapX, int offsetX) {
        return (int) ((super.mapToScreenX(mapX, offsetX) / _camera.zoom) + ((applicationConfig.getResolutionWidth() - (applicationConfig.getResolutionWidth() / _camera.zoom)) / 2));
    }

    protected int mapToScreenY(int mapY, int offsetY) {
        return (int) ((super.mapToScreenY(mapY, offsetY) / _camera.zoom) + ((applicationConfig.getResolutionHeight() - (applicationConfig.getResolutionHeight() / _camera.zoom)) / 2));
    }

}