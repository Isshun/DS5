//package org.smallbox.faraway.modules.building;
//
//import org.smallbox.faraway.core.dependencyInjector.BindModule;
//import org.smallbox.faraway.core.engine.module.GameModule;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.module.path.PathManager;
//import org.smallbox.faraway.modules.area.AreaModule;
//import org.smallbox.faraway.modules.consumable.ConsumableModule;
//import org.smallbox.faraway.modules.consumable.StorageArea;
//import org.smallbox.faraway.modules.item.ItemModule;
//import org.smallbox.faraway.modules.job.JobModule;
//import org.smallbox.faraway.modules.structure.StructureModule;
//import org.smallbox.faraway.modules.world.WorldModule;
//
///**
// * Created by Alex on 02/03/2017.
// */
//public class BuildingModule extends GameModule {
//
//    @Inject
//    private WorldModule worldModule;
//
//    @Inject
//    private ItemModule itemModule;
//
//    @Inject
//    private StructureModule structureModule;
//
//    @Inject
//    private JobModule jobModule;
//
//    @Inject
//    private AreaModule areaModule;
//
//    @Inject
//    private PathManager pathManager;
//
//    @Override
//    public void onGameCreate(Game game) {
//        areaModule.addAreaClass(StorageArea.class);
//    }
//
//    @Override
//    public void onModuleUpdate(Game game) {
//    }
//
//}
