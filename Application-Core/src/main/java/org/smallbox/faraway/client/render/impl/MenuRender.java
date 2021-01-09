package org.smallbox.faraway.client.render.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.menu.controller.MenuMainController;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.RootView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class MenuRender {

    @Inject
    private GDXRenderer gdxRenderer;

    @Inject
    private MenuMainController menuMainController;

    @Inject
    private ShortcutManager shortcutManager;

    @Inject
    private UIManager uiManager;

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

    public void render() {
        Gdx.input.setInputProcessor(_menuInputAdapter);
        Gdx.gl.glClearColor(.07f, 0.1f, 0.12f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render application
        gdxRenderer.clear();
        gdxRenderer.refresh();
        uiManager.getMenuViews().forEach((name, view) -> view.draw(gdxRenderer, 0, 0));
    }

    private boolean clickOn(View view, int screenX, int screenY) {
        if (view.getEvents().hasClickListener() && view.isVisible() && hasHierarchyVisible(view) && view.contains(screenX, screenY)) {
            view.click(screenX, screenY);
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
