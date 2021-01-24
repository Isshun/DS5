package org.smallbox.faraway.client.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.smallbox.faraway.client.AssetManager;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.RootView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.OnInit;

@ApplicationObject
public class MenuRender {
    @Inject private org.smallbox.faraway.client.render.UIRendererManager UIRendererManager;
    @Inject private ShortcutManager shortcutManager;
    @Inject private UIManager uiManager;
    @Inject private AssetManager assetManager;

    private Sprite sprite;

    private final InputProcessor _menuInputAdapter = new InputAdapter() {
        public boolean touchUp (int screenX, int screenY, int pointer, int button) {
            for (RootView rootView: uiManager.getMenuViews().values()) {
                if (clickOn(rootView.getView(), screenX, screenY)) {
                    return true;
                }
            }
            return false;
        }

        public boolean keyUp (int keycode) {
            shortcutManager.action(keycode);
            return false;
        }
    };

    @OnInit
    public void init() {
        assetManager.load("data/background/17520.jpg", Texture.class);
        assetManager.finishLoading();

        sprite = new Sprite(assetManager.get("data/background/17520.jpg", Texture.class));
        sprite.flip(false, true);
        sprite.setScale(1.1f);
        sprite.setPosition(0, 0);

    }

    public void render() {
        Gdx.input.setInputProcessor(_menuInputAdapter);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        UIRendererManager.clear();
        UIRendererManager.refresh();

        if (sprite != null) {
            UIRendererManager.drawSprite(sprite);
            sprite.scale(0.00001f);
            sprite.translate(0.02f, 0.035f);
        }

        uiManager.getMenuViews().forEach((name, view) -> view.draw(UIRendererManager, 0, 0));
    }

    private boolean clickOn(View view, int screenX, int screenY) {
        if (view.getEvents().hasClickListener() && view.isVisible() && hasHierarchyVisible(view) && view.contains(screenX, screenY)) {
            view.getEvents().click(screenX, screenY);
            return true;
        }

        if (view instanceof CompositeView && ((CompositeView)view).getViews() != null) {
            for (View subView: ((CompositeView)view).getViews()) {
                if (clickOn(subView, screenX, screenY)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasHierarchyVisible(View view) {
        if (view.getParent() != null) {
            return hasHierarchyVisible(view.getParent());
        }
        return view.isVisible();
    }

}
