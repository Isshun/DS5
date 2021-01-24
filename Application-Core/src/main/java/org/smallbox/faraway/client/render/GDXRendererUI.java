package org.smallbox.faraway.client.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.util.log.Log;

@ApplicationObject
public class GDXRendererUI extends GDXRendererBase {

    public void init() {
        super.init();
        _cameraUI = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _cameraUI.zoom = 1f;
    }

    public float getZoom() {
        return _camera.zoom;
    }

    public void setZoom(float zoom) {
        _camera.zoom = zoom;
    }

    public Camera getCamera() {
        return _camera;
    }

    public void refresh() {
        _camera.update();
        _cameraUI.update();
        _batch.setProjectionMatrix(_camera.combined);
    }

    public void zoomOut() {
        _camera.zoom = Math.min(_camera.zoom + ZOOM_INTERVAL, MAX_ZOOM_OUT);
        viewport.refresh();
        Log.info("Set zoom: " + _camera.zoom);
    }

    public void zoomIn() {
        _camera.zoom = Math.max(_camera.zoom - ZOOM_INTERVAL, MAX_ZOOM_IN);
        viewport.refresh();
        Log.info("Set zoom: " + _camera.zoom);
    }

    public void draw(View view, int x, int y) {
        view.draw(this, x, y);
    }

}