package org.smallbox.faraway.game.module.dev;

import org.smallbox.faraway.core.ui.GDXLabel;
import org.smallbox.faraway.engine.renderer.BaseRenderer;
import org.smallbox.faraway.engine.renderer.MainRenderer;
import org.smallbox.faraway.game.module.*;
import org.smallbox.faraway.ui.engine.view.FrameLayout;
import org.smallbox.faraway.ui.engine.view.UILabel;

/**
 * Created by Alex on 30/08/2015.
 */
public class RenderManagerModule extends GameUIModule {
    @Override
    protected boolean loadOnStart() {
        return false;
    }

    @Override
    protected void onCreate() {
        addWindow(WindowBuilder.create()
                .setTitle("Render Manager")
                .setMovable(true)
                .setClosable(true)
                .build(new WindowListener() {
            @Override
            public void onCreate(UIWindow window, FrameLayout view) {
                window.setPosition(500, 500);

                int index = 0;
                for (BaseRenderer render: MainRenderer.getInstance().getRenders()) {
                    UILabel lbModule = new GDXLabel();
                    lbModule.setString((render.isLoaded() ? "[x] " : "[ ] ") + render.getClass().getSimpleName());
                    lbModule.setCharacterSize(14);
                    lbModule.setPosition(0, index++ * 20);
                    lbModule.setOnClickListener(view1 -> {
                        MainRenderer.getInstance().toggleRender(render);
                        lbModule.setString((render.isLoaded() ? "[x] " : "[ ] ") + render.getClass().getSimpleName());
                    });
                    view.addView(lbModule);
                }

                view.setSize(150, index * 20);
            }

            @Override
            public void onRefresh(int update) {
            }

            @Override
            public void onClose() {
            }
        }));
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected void onUpdate(int tick) {
    }
}
