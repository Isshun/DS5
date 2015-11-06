package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.Data;
import org.smallbox.faraway.core.module.GameModule;
import org.smallbox.faraway.core.module.java.ModuleManager;
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
            this.modifier = modifier != null ? modifier : GameEventListener.Modifier.NONE;
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
            new ApplicationShortcut(GameEventListener.Key.F5, GameEventListener.Modifier.ALT, () -> {
                Game.getInstance().save(Game.getInstance().getFileName());
            }),
            new ApplicationShortcut(GameEventListener.Key.ENTER, GameEventListener.Modifier.ALT, () -> {
//            _isFullscreen = !_isFullscreen;
//            _renderer.setFullScreen(_isFullscreen);
            }),
            new ApplicationShortcut(GameEventListener.Key.F1, GameEventListener.Modifier.ALT, () -> {
                Application.getInstance().newGame("14.sav", Data.getData().getRegion("base.planet.arrakis", "desert"));
            }),
            new ApplicationShortcut(GameEventListener.Key.F4, GameEventListener.Modifier.ALT, () -> {
                Application.getInstance().setRunning(false);
            }),
            new ApplicationShortcut(GameEventListener.Key.ESCAPE, GameEventListener.Modifier.NONE , () -> {
                if (GameManager.getInstance().isRunning()) {
                    if (GameManager.getInstance().isPaused() && UserInterface.getInstance().findById("base.ui.menu_pause").isVisible()) {
                        GameManager.getInstance().setPause(false);
                        return;
                    }
                    if (!GameManager.getInstance().isPaused()
                            && UserInterface.getInstance().findById("panel_main").isVisible()
                            && UserInterface.getInstance().getSelector().isClear()) {
                        GameManager.getInstance().setPause(true);
                        return;
                    }
                }
            }),
    };

    public static void onKeyPress(GameEventListener.Key key, GameEventListener.Modifier modifier) {
        for (ApplicationShortcut shortcut: SHORTCUTS) {
            if (shortcut.key == key && shortcut.modifier == modifier) {
                shortcut.runnable.run();
            }
        }

        Data.getData().bindings.stream()
                .filter(binding -> binding.key == key && binding.modifier == modifier)
                .forEach(binding -> Application.getInstance().notify(observer -> observer.onBindingPress(binding)));
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