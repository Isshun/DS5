package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@GameObject
public class ShortcutDashboardLayer extends DashboardLayerBase {
    @Inject private ShortcutManager shortcutManager;

    @Override
    protected void onDraw(GDXRendererBase renderer, int frame) {
        shortcutManager.getBindings().forEach(strategy -> drawDebug(renderer, "SHORTCUT", strategy.label.replace("org.smallbox.faraway.", "") + " -> " + strategy.key));
    }

}
