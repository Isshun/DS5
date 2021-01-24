package org.smallbox.faraway.game.plant;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.game.area.AreaModuleBase;

@GameObject
public class GardenModule extends AreaModuleBase<GardenArea> {

    @Override
    public GardenArea onNewArea() {
        return new GardenArea();
    }

}
