package org.smallbox.faraway.game.module.dev;

import org.smallbox.faraway.core.ui.GDXLabel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.*;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 30/08/2015.
 */
public class ModuleManagerModule extends GameUIModule {
    @Override
    protected boolean loadOnStart() {
        return false;
    }

    @Override
    protected void onLoaded() {
        addWindow(new UITitleWindow() {
            @Override
            protected String getTitle() {
                return "Module Manager";
            }

            @Override
            protected boolean isClosable() {
                return true;
            }

            @Override
            protected boolean isMovable() {
                return true;
            }

            @Override
            protected void onClose() {

            }

            @Override
            protected void onCreate(UIWindow window, FrameLayout content) {
                window.setPosition(500, 500);

                int index = 0;
                for (GameModule module : Game.getInstance().getModules()) {
                    UILabel lbModule = new GDXLabel();
                    lbModule.setString((module.isLoaded() ? "[x] " : "[ ] ") + module.getClass().getSimpleName());
                    lbModule.setCharacterSize(14);
                    lbModule.setPosition(0, index++ * 20);
                    lbModule.setOnClickListener(view1 -> {
                        Game.getInstance().toggleModule(module);
                        lbModule.setString((module.isLoaded() ? "[x] " : "[ ] ") + module.getClass().getSimpleName());
                    });
                    content.addView(lbModule);
                }

                content.setSize(150, index * 20);
            }

            @Override
            protected void onRefresh(int update) {

            }

            @Override
            protected String getContentLayout() {
                return null;
            }
        });
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
