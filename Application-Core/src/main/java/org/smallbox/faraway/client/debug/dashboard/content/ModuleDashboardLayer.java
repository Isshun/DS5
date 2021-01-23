package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.engine.module.AbsGameModule;
import org.smallbox.faraway.core.game.Game;

import java.util.Comparator;

@GameObject
public class ModuleDashboardLayer extends DashboardLayerBase {
    @Inject private Game game;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        game.getModules().stream()
                .sorted(Comparator.comparingLong(AbsGameModule::getCumulateTime).reversed())
                .forEach(module -> drawDebug(renderer, "MODULE",
                        String.format("%-32s total: %-5d med: %.2f",
                                module.getName(),
                                module.getCumulateTime() / 1000,
                                module.getCumulateTime() / 1000 / (double)game.getTick())));
    }

}
