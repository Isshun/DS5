package org.smallbox.faraway.game.module;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.GDXRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public abstract class GameUIModule extends GameModule {
    private List<UIWindow> _windows = new ArrayList<>();

    public void draw(GDXRenderer renderer) {
        _windows.forEach(view -> view.draw(renderer, 0, 0));
    }

    protected void addWindow(UIWindow window) {
        _windows.add(window);
        window.create();
    }

    @Override
    public void destroy() {
        super.destroy();
        _windows.clear();
    }

    @Override
    public boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y) {
        for (UIWindow window: _windows) {
            if (window.onMouseEvent(action, button, x, y)) {
                return true;
            }
        }
        return false;
    }

    public void refresh(int update) {
        _windows.forEach(window -> window.refresh(update));
    }
}
