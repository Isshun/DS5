package org.smallbox.faraway.client.render;

import com.badlogic.gdx.InputAdapter;
import org.smallbox.faraway.client.shortcut.ShortcutManager;
import org.smallbox.faraway.client.ui.RootView;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@ApplicationObject
public class MenuRenderInputProcessor extends InputAdapter {
    @Inject private UIEventManager uiEventManager;
    @Inject private ShortcutManager shortcutManager;
    @Inject private UIManager uiManager;
    @Inject private MenuRender menuRender;

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (RootView rootView : uiManager.getMenuViews().values()) {
            if (menuRender.clickOn(rootView.getView(), screenX, screenY)) {
                return true;
            }
        }
        uiEventManager.onMouseRelease(screenX, screenY, button);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (menuRender.nextKeyConsumer != null) {
            menuRender.nextKeyConsumer.accept(keycode, null);
            return false;
        }

        shortcutManager.action(keycode);
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        uiEventManager.onMouseMove(x, y, false);
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        uiEventManager.onMousePress(x, y, button);
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        uiEventManager.onDrag(x, y, false);
        return false;
    }

}
