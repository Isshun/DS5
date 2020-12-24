package org.smallbox.faraway.client.debug.dashboard.content;

import org.smallbox.faraway.client.debug.dashboard.DashboardLayerBase;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.modules.plant.PlantModule;

@GameObject
public class PlantDashboardLayer extends DashboardLayerBase {

    @Inject
    private PlantModule plantModule;

    @Override
    protected void onDraw(GDXRenderer renderer, int frame) {
        if (plantModule != null && plantModule.getPlants() != null) {
            plantModule.getPlants().forEach(plant -> drawDebug(renderer, "Plant", plant.getLabel() + " " + plant.getMaturity()));
        }
    }

}
