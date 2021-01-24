package org.smallbox.faraway.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;
import org.smallbox.faraway.util.log.Log;

@ApplicationObject
public class WorldCameraManager {
    protected final static float MAX_ZOOM_IN = 1.5f;
    protected final static float MAX_ZOOM_OUT = 3f;
    protected final static float ZOOM_INTERVAL = 0.125f;

    @Inject Viewport viewport;

    private OrthographicCamera _camera;

    @OnInit
    public void init() {
        _camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        _camera.zoom = 1.5f;
    }

    public Matrix4 getCombinedProjection() {
        return _camera.combined;
    }

    public Vector3 getPosition() {
        return _camera.position;
    }

    public float getZoom() {
        return _camera.zoom;
    }

    public void setZoom(float zoom) {
        _camera.zoom = zoom;
    }

    public void update() {
        _camera.update();
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

}
