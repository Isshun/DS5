package org.smallbox.faraway.core;

import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.engine.module.GameModule;
import org.smallbox.faraway.core.engine.module.java.ModuleManager;
import org.smallbox.faraway.core.engine.renderer.GDXRenderer;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.GameInfo;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.GameObserver;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.ui.UserInterface;

import static org.smallbox.faraway.core.engine.GameEventListener.Key.*;
import static org.smallbox.faraway.core.engine.GameEventListener.Modifier.ALT;
import static org.smallbox.faraway.core.engine.GameEventListener.Modifier.NONE;

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
            this.modifier = modifier != null ? modifier : NONE;
            this.runnable = runnable;
        }
    }

    private static ApplicationShortcut[] SHORTCUTS = new ApplicationShortcut[] {
            new ApplicationShortcut(RIGHT, null, () -> {
                Game.getInstance().getViewport().startMove(0, 0);
                Game.getInstance().getViewport().update(100, 0);
            }),
            new ApplicationShortcut(F10, null, () -> {
                GameModule debugModule = ModuleManager.getInstance().getModule("DebugModule");
                if (debugModule != null && debugModule.isLoaded()) {
                    ModuleManager.getInstance().unloadModule(debugModule);
                } else if (debugModule != null) {
                    ModuleManager.getInstance().loadModule(debugModule);
                }
            }),
            new ApplicationShortcut(F5, NONE, () -> {
                GameManager.getInstance().saveGame(Game.getInstance().getInfo(), GameInfo.Type.FAST);
            }),
            new ApplicationShortcut(ENTER, ALT, () -> {
//            _isFullscreen = !_isFullscreen;
//            _renderer.setFullScreen(_isFullscreen);
            }),
            new ApplicationShortcut(PAGEUP, NONE, () -> {
                Application.getInstance().notify(GameObserver::onFloorUp);
            }),
            new ApplicationShortcut(PAGEDOWN, NONE, () -> {
                Application.getInstance().notify(GameObserver::onFloorDown);
            }),
//            new ApplicationShortcut(GameEventListener.Key.F1, GameEventListener.Modifier.ALT, () -> {
//                Application.getInstance().newGame("14.sav", Data.getData().getRegion("base.planet.arrakis", "desert"));
//            }),
            new ApplicationShortcut(F4, ALT, () -> {
                Application.getInstance().setRunning(false);
            }),

            new ApplicationShortcut(ESCAPE, NONE, () -> Data.getData().getBinding("base.binding.open_panel_main").action()),
            new ApplicationShortcut(B, NONE, () -> Data.getData().getBinding("base.binding.open_panel_build").action()),
            new ApplicationShortcut(P, NONE, () -> Data.getData().getBinding("base.binding.open_panel_plan").action()),
            new ApplicationShortcut(T, NONE, () -> Data.getData().getBinding("base.binding.open_panel_jobs").action()),
            new ApplicationShortcut(C, NONE, () -> Data.getData().getBinding("base.binding.open_panel_crew").action()),

            new ApplicationShortcut(F1, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_areas").action()),
            new ApplicationShortcut(F2, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_rooms").action()),
            new ApplicationShortcut(F3, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_temperature").action()),
            new ApplicationShortcut(F4, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_oxygen").action()),
            new ApplicationShortcut(F5, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_water").action()),
            new ApplicationShortcut(F6, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_security").action()),
            new ApplicationShortcut(F12, NONE, () -> Data.getData().getBinding("base.binding.toggle_display_debug").action()),

            new ApplicationShortcut(ESCAPE, NONE , () -> {
                if (GameManager.getInstance().isLoaded()) {
                    if (!Game.getInstance().getInteraction().isClear()) {
                        Game.getInstance().getInteraction().clear();
                        return;
                    }

                    if (!Game.getInstance().getSelector().isClear()) {
                        Game.getInstance().getSelector().clear();
                        return;
                    }

                    if (!GameManager.getInstance().isRunning() && UserInterface.getInstance().findById("base.ui.menu_pause").isVisible()) {
                        GameManager.getInstance().setRunning(true);
                        return;
                    }

                    if (GameManager.getInstance().isRunning() && UserInterface.getInstance().findById("base.ui.panel_main").isVisible()) {
                        GameManager.getInstance().setRunning(false);
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

    public static boolean onMouseEvent(GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        if (button == GameEventListener.MouseButton.RIGHT && action == GameEventListener.Action.PRESSED) {
            if (GameManager.getInstance().isLoaded()) {
                GameManager.getInstance().getGame().getViewport().startMove(x, y);
            }
            return true;
        }
        if (button == GameEventListener.MouseButton.WHEEL_UP) {
            GDXRenderer.getInstance().zoomUp();
            return true;
        }
        else if (button == GameEventListener.MouseButton.WHEEL_DOWN) {
            GDXRenderer.getInstance().zoomDown();
            return true;
        }

        return false;
    }
}