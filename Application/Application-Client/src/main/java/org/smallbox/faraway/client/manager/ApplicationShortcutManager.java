package org.smallbox.faraway.client.manager;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.GameInfo;

import static org.smallbox.faraway.core.engine.GameEventListener.Key.F5;
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
//            new ApplicationShortcut(RIGHT, null, () -> {
//                ApplicationClient.mainRenderer.getViewport().startMove(0, 0);
//                ApplicationClient.mainRenderer.getViewport().update(100, 0);
//            }),
//            new ApplicationShortcut(F10, null, () -> {
//                ModuleBase debugModule = Application.moduleManager.getModule("DebugModule");
//                if (debugModule != null && debugModule.isLoaded()) {
//                    Application.moduleManager.unloadModule(debugModule);
//                } else if (debugModule != null) {
//                    Application.moduleManager.loadModule(debugModule);
//                }
//            }),
            new ApplicationShortcut(F5, NONE, () -> Application.gameSaveManager.saveGame(Application.gameManager.getGame(), Application.gameManager.getGame().getInfo(), GameInfo.Type.FAST)),
//            new ApplicationShortcut(ENTER, ALT, () -> {
////            _isFullscreen = !_isFullscreen;
////            _renderer.setFullScreen(_isFullscreen);
//            }),
//            new ApplicationShortcut(PAGEUP, NONE, () -> ApplicationClient.notify(GameClientObserver::onFloorUp)),
//            new ApplicationShortcut(PAGEDOWN, NONE, () -> ApplicationClient.notify(GameClientObserver::onFloorDown)),
////            new ApplicationShortcut(GameEventListener.Key.F1, GameEventListener.Modifier.ALT, () -> {
////                Application.newGame("14.sav", Application.data.getRegion("base.planet.arrakis", "desert"));
////            }),
//            new ApplicationShortcut(F4, ALT, () -> Application.setRunning(false)),
//            new ApplicationShortcut(SPACE, NONE, () -> Application.data.getBinding("base.binding.toggle_speed_0").action()),
//            new ApplicationShortcut(D_1, NONE, () -> Application.data.getBinding("base.binding.set_speed_1").action()),
//            new ApplicationShortcut(D_2, NONE, () -> Application.data.getBinding("base.binding.set_speed_2").action()),
//            new ApplicationShortcut(D_3, NONE, () -> Application.data.getBinding("base.binding.set_speed_3").action()),
//            new ApplicationShortcut(D_4, NONE, () -> Application.data.getBinding("base.binding.set_speed_4").action()),
//
//            new ApplicationShortcut(ESCAPE, NONE, () -> Application.data.getBinding("base.binding.open_panel_main").action()),
//            new ApplicationShortcut(B, NONE, () -> Application.data.getBinding("base.binding.open_panel_build").action()),
////            new ApplicationShortcut(P, NONE, () -> Application.data.getBinding("base.binding.open_panel_plan").action()),
////            new ApplicationShortcut(T, NONE, () -> Application.data.getBinding("base.binding.open_panel_jobs").action()),
//            new ApplicationShortcut(C, NONE, () -> Application.data.getBinding("base.binding.open_panel_crew").action()),
//
////            new ApplicationShortcut(F1, NONE, () -> Application.data.getBinding("base.binding.toggle_display_areas").action()),
////            new ApplicationShortcut(F2, NONE, () -> Application.data.getBinding("base.binding.toggle_display_rooms").action()),
////            new ApplicationShortcut(F3, NONE, () -> Application.data.getBinding("base.binding.toggle_display_temperature").action()),
////            new ApplicationShortcut(F4, NONE, () -> Application.data.getBinding("base.binding.toggle_display_oxygen").action()),
////            new ApplicationShortcut(F5, NONE, () -> Application.data.getBinding("base.binding.toggle_display_water").action()),
////            new ApplicationShortcut(F6, NONE, () -> Application.data.getBinding("base.binding.toggle_display_security").action()),
////            new ApplicationShortcut(F12, NONE, () -> Application.data.getBinding("base.binding.toggle_display_debug").action()),
//
//            new ApplicationShortcut(ESCAPE, NONE , () -> {
//                if (Application.gameManager.isLoaded()) {
////                    if (!Application.gameManager.getGame().getInteraction().isClear()) {
////                        Application.gameManager.getGame().getInteraction().clear();
////                        return;
////                    }
////
////                    if (!Application.gameManager.getGame().getSelector().isClear()) {
////                        Application.gameManager.getGame().getSelector().clear();
////                        return;
////                    }
//
//                    if (!Application.gameManager.isRunning() && ApplicationClient.uiManager.findById("base.ui.menu_pause").isVisible()) {
//                        Application.gameManager.setRunning(true);
//                        return;
//                    }
//
//                    if (Application.gameManager.isRunning() && ApplicationClient.uiManager.findById("base.ui.panel_main").isVisible()) {
//                        Application.gameManager.setRunning(false);
//                        return;
//                    }
//                }
//            }),
    };

    public static void onKeyPress(GameEventListener.Key key, GameEventListener.Modifier modifier) {
        for (ApplicationShortcut shortcut: SHORTCUTS) {
            if (shortcut.key == key && shortcut.modifier == modifier) {
                shortcut.runnable.run();
            }
        }

        Application.data.bindings.stream()
                .filter(binding -> binding.key == key && binding.modifier == modifier)
                .forEach(binding -> Application.notify(observer -> observer.onBindingPress(binding)));
    }

    public static boolean onMouseEvent(GameEvent event, GameEventListener.Action action, GameEventListener.MouseButton button, int x, int y, boolean rightPressed) {
        if (button == GameEventListener.MouseButton.RIGHT && action == GameEventListener.Action.PRESSED) {
            if (Application.gameManager.isLoaded()) {
                ApplicationClient.mainRenderer.getViewport().startMove(x, y);
            }
            return true;
        }
        if (button == GameEventListener.MouseButton.WHEEL_UP) {
            ApplicationClient.gdxRenderer.zoomUp();
            return true;
        }
        else if (button == GameEventListener.MouseButton.WHEEL_DOWN) {
            ApplicationClient.gdxRenderer.zoomDown();
            return true;
        }

        return false;
    }
}