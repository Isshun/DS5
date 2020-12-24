package org.smallbox.faraway.module.dev.controller.toolbox;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.world.WorldInteractionModule;

public class DebugConsumableController extends LuaController {
    @BindModule
    private ConsumableModule _consumableModule;

    @BindModule
    private WorldInteractionModule _worldInteraction;

    @BindLua
    private UIList listConsumables;

    private ItemInfo _consumable;

//    @Override
//    public void onGameCreate(Game game) {
//        Application.data.getItems().stream()
//                .filter(itemInfo -> itemInfo.isConsumable)
//                .forEach(itemInfo -> listConsumables.addView(UILabel.create(null)
//                        .setText(itemInfo.label)
//                        .setSize(100, 22)
//                        .setOnClickListener((GameEvent event) -> {
//                            Log.info("select consumable: " + itemInfo.name);
//                            _consumable = itemInfo;
//                        })));
//
//        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
//            @Override
//            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
//                if (_consumable != null) {
//                    Log.info("put consumable on map: " + _consumable.name);
//                    parcels.forEach(parcel -> {
//                        _consumableModule.create(_consumable, 100, parcel);
//                        _consumable = null;
//                    });
//                }
//            }
//        });
//    }
}
