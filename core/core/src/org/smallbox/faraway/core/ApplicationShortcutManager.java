package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.module.GameModule;
import org.smallbox.faraway.core.game.module.ModuleManager;
import org.smallbox.faraway.ui.UserInterface;

/**
 * Created by Alex on 20/10/2015.
 */
public class ApplicationShortcutManager {
    private static class ApplicationShortcut {
        public final GameEventListener.Key          key;
        public final GameEventListener.Modifier     modifier;
        public final Runnable                       runnable;

        public ApplicationShortcut(GameEventListener.Key key, GameEventListener.Modifier modifier, Runnable runnable) {
            this.key = key;
            this.modifier = modifier;
            this.runnable = runnable;
        }
    }

    private static ApplicationShortcut[] SHORTCUTS = new ApplicationShortcut[] {
        new ApplicationShortcut(GameEventListener.Key.RIGHT, null, () -> {
            Game.getInstance().getViewport().startMove(0, 0);
            Game.getInstance().getViewport().update(100, 0);
        }),
        new ApplicationShortcut(GameEventListener.Key.F10, null, () -> {
            GameModule debugModule = ModuleManager.getInstance().getModule("DebugModule");
            if (debugModule != null && debugModule.isLoaded()) {
                ModuleManager.getInstance().unloadModule(debugModule);
            } else if (debugModule != null) {
                ModuleManager.getInstance().loadModule(debugModule);
            }
        }),
        new ApplicationShortcut(GameEventListener.Key.F5, null, () -> {
            Game.getInstance().save(Game.getInstance().getFileName());
        }),
        new ApplicationShortcut(GameEventListener.Key.ENTER, GameEventListener.Modifier.ALT, () -> {
//            _isFullscreen = !_isFullscreen;
//            _renderer.setFullScreen(_isFullscreen);
        }),
    };

    public static void onKeyPress(GameEventListener.Key key, GameEventListener.Modifier modifier) {
        for (ApplicationShortcut shortcut: SHORTCUTS) {
            if (shortcut.key == key && shortcut.modifier == modifier) {
                shortcut.runnable.run();
            }
        }
    }

    public static void onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        if (button == GameEventListener.MouseButton.RIGHT && action == GameEventListener.Action.PRESSED) {
            if (GameManager.getInstance().isRunning()) {
                GameManager.getInstance().getGame().getViewport().startMove(x, y);
            }
            return;
        }
        if (button == GameEventListener.MouseButton.WHEEL_UP) {
            GDXRenderer.getInstance().zoomUp();
        }
        else if (button == GameEventListener.MouseButton.WHEEL_DOWN) {
            GDXRenderer.getInstance().zoomDown();
        }
        else {
            UserInterface.getInstance().onMouseEvent(action, button, x, y, rightPressed);
        }
    }
}
