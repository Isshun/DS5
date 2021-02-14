package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.renderer.BaseRenderer;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.MonitoringManager;

@GameObject
public class ModuleDashboardLayer extends DashboardLayerBase {
    @Inject private DependencyManager dependencyManager;
    @Inject private MonitoringManager monitoringManager;
    @Inject private Game game;

    @Override
    protected void onDraw(BaseRenderer renderer, int frame) {
        monitoringManager.entries.values().stream()
                .sorted((o1, o2) -> (int) (o2.footPrint - o1.footPrint))
                .forEach(entry -> drawDebug(renderer, "MODULE",
                        String.format("%-42s %-16s %-16s %-16s %-16s",
                                entry.model.getClass().getSimpleName(),
                                entry.getFootPrintFormatted(),
                                "(" + entry.getFootPrintFormattedByCycle() + ")",
                                entry.duration + " ms",
                                "(" + entry.duration / entry.calls + " ms)"
                        )));
//                        String.format("%-42s %-16d total: %-5d med: %.2f",
//                                module.getName(),
//                                module.getFootprint() / 1000,
//                                module.getCumulateTime() / 1000,
//                                module.getCumulateTime() / 1000 / (double)game.getTick())));
//        dependencyManager.getSubTypesOf(AbsGameModule.class).stream()
//                .sorted(Comparator.comparingLong(AbsGameModule::getFootprint).reversed())
//                .forEach(module -> drawDebug(renderer, "MODULE",
//                        String.format("%-42s %-16d total: %-5d med: %.2f",
//                                module.getName(),
//                                module.getFootprint() / 1000,
//                                module.getCumulateTime() / 1000,
//                                module.getCumulateTime() / 1000 / (double)game.getTick())));
    }

}
