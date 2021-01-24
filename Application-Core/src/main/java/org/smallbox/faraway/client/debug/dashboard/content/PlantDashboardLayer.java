package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.GDXRendererBase;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.modules.plant.PlantModule;

@GameObject
public class PlantDashboardLayer extends DashboardLayerBase {
    @Inject private PlantModule plantModule;

    @Override
    protected void onDraw(GDXRendererBase renderer, int frame) {
        plantModule.getAll().forEach(plant -> drawDebug(renderer, "Plant", plant.getLabel() + " " + plant.getMaturity()));
    }

}
