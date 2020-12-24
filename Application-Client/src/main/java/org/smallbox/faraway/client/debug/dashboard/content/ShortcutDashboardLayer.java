package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.manager.ShortcutManager;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;

@GameObject
public class ShortcutDashboardLayer extends DashboardLayerBase {

    @Inject
    private ShortcutManager shortcutManager;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        shortcutManager.getBindings().forEach(strategy -> drawDebug(renderer, "SHORTCUT", strategy.label.replace("org.smallbox.faraway.", "") + " -> " + strategy.key));
    }

}
