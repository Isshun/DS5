package org.smallbox.faraway.game.module.dev;

import org.smallbox.faraway.core.ui.GDXLabel;
import org.smallbox.faraway.game.Game;
import org.smallbox.faraway.game.module.GameModule;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 30/08/2015.
 */
public class ModuleManagerModule extends GameUIModule {
    @Override
    protected void onCreate() {
        createPanel("test.yml", (layout, view) -> {
            int index = 0;
            FrameLayout frameContent = (FrameLayout)view.findById("content");
            for (GameModule module: Game.getInstance().getModules()) {
                UILabel lbModule = new GDXLabel();
                lbModule.setString((module.isLoaded() ? "[x] " : "[ ] ") + module.getClass().getSimpleName());
                lbModule.setCharacterSize(14);
                lbModule.setPosition(0, index++ * 20);
                lbModule.setOnClickListener(view1 -> {
                    if (module.isLoaded()) {
                        Game.getInstance().removeModule(module);
                        lbModule.setString("[ ] " + module.getClass().getSimpleName());
                    } else {
                        Game.getInstance().insertModule(module);
                        lbModule.setString("[x] " + module.getClass().getSimpleName());
                    }
                });
                frameContent.addView(lbModule);
            }
            frameContent.setSize(150, index * 20);
        });
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
