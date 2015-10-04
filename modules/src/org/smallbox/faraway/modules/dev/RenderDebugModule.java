package org.smallbox.faraway.modules.dev;

import org.smallbox.faraway.engine.GameEventListener;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.module.GameUIModule;
import org.smallbox.faraway.game.module.UITitleWindow;
import org.smallbox.faraway.game.module.UIWindow;
import org.smallbox.faraway.ui.engine.view.UIFrame;
import org.smallbox.faraway.ui.engine.view.UILabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 30/08/2015.
 */
public class RenderDebugModule extends GameUIModule {
    public class RenderDebugModuleWindow extends UITitleWindow {
        @Override
        protected void onCreate(UIWindow window, UIFrame content) {
            window.setPosition(500, 500);

            List<UILabel> entries = new ArrayList<>();
            for (BaseRenderer render: MainRenderer.getInstance().getRenders()) {
                UILabel lbModule = new UILabel();
                lbModule.setData(render.getClass().getSimpleName());
                lbModule.setText((render.isLoaded() ? "[x] " : "[ ] ") + render.getClass().getSimpleName());
                lbModule.setTextSize(14);
                lbModule.setOnClickListener(view1 -> {
                    MainRenderer.getInstance().toggleRender(render);
                    lbModule.setText((render.isLoaded() ? "[x] " : "[ ] ") + render.getClass().getSimpleName());
                });
                entries.add(lbModule);
                content.addView(lbModule);
            }

            entries.sort((l1, l2) -> ((String) l1.getData()).compareTo((String)l2.getData()));
            entries.forEach(entry -> entry.setPosition(0, entries.indexOf(entry) * 20));

            content.setSize(150, entries.size() * 20);
        }

        @Override
        protected void onRefresh(int update) {

        }

        @Override
        protected String getContentLayout() {
            return null;
        }

        @Override
        protected String getTitle() {
            return "Render Manager";
        }

        @Override
        protected boolean isClosable() {
            return false;
        }

        @Override
        protected boolean isMovable() {
            return false;
        }

        @Override
        protected void onClose() {

        }
    }

    @Override
    protected boolean loadOnStart() {
        return false;
    }

    @Override
    protected void onLoaded() {
        addWindow(new RenderDebugModuleWindow());
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onUpdate(int tick) {
    }

    @Override
    public boolean onKey(GameEventListener.Key key) {
        if (key == GameEventListener.Key.F11) {
            _windows.forEach(window -> window.setVisible(!window.isVisible()));
            return true;
        }
        return false;
    }
}
