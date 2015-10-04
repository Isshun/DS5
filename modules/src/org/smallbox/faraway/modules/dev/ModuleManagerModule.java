package org.smallbox.faraway.modules.dev;

import org.smallbox.faraway.core.ui.GDXLabel;
import org.smallbox.faraway.engine.Color;
import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.game.module.*;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.ArrayList;
import java.util.List;

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

                List<UILabel> entries = new ArrayList<>();
                for (GameModule module : ModuleManager.getInstance().getModulesBase()) {
                    UILabel lbModule = createModuleView(module, module.getClass().getSimpleName(), false);
                    content.addView(lbModule);
                    entries.add(lbModule);
                }
                for (GameModule module : ModuleManager.getInstance().getModulesThird()) {
                    UILabel lbModule = createModuleView(module, module.getClass().getSimpleName(), true);
                    content.addView(lbModule);
                    entries.add(lbModule);
                }

                entries.sort((l1, l2) -> ((String) l1.getData()).compareTo((String) l2.getData()));
                entries.forEach(entry -> entry.setPosition(0, entries.indexOf(entry) * 20));

                content.setSize(150, entries.size() * 20);
            }

            private UILabel createModuleView(GameModule module, String name, boolean isThirdParty) {
                UILabel lbModule = new GDXLabel();
                lbModule.setData(name);
                lbModule.setText((module.isLoaded() ? "[x] " : "[ ] ") + name);
                if (isThirdParty) {
                    lbModule.setTextColor(new Color(0xffcc88));
                }
                lbModule.setTextSize(14);
                lbModule.setOnClickListener(view1 -> {
                    ModuleManager.getInstance().toggleModule(module);
                    lbModule.setText((module.isLoaded() ? "[x] " : "[ ] ") + name);
                });
                return lbModule;
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

    @Override
    public boolean onKey(GameEventListener.Key key) {
        if (key == GameEventListener.Key.F12) {
            _windows.forEach(window -> window.setVisible(!window.isVisible()));
            return true;
        }
        return false;
    }

}
