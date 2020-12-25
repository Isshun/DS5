package org.smallbox.faraway.modules.plant;

import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.modules.area.AreaModuleBase;

@GameObject
public class GardenModule extends AreaModuleBase<GardenArea> {

    @Override
    public GardenArea onNewArea() {
        return new GardenArea();
    }

}
