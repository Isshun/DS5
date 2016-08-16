package org.smallbox.faraway.module.dev.controller.toolbox;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.core.util.Log;
import org.smallbox.faraway.module.consumable.ConsumableModule;
import org.smallbox.faraway.module.world.WorldInteractionModule;
import org.smallbox.faraway.module.world.WorldInteractionModuleObserver;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.ui.engine.views.widgets.UIList;

import java.util.Collection;

/**
 * Created by Alex on 31/07/2016.
 */
public class DebugConsumableController extends LuaController {
    @BindModule
    private ConsumableModule _consumableModule;

    @BindModule
    private WorldInteractionModule _worldInteraction;

    @BindLua
    private UIList listConsumables;

    private ItemInfo _consumable;

    @Override
    protected void onGameCreate(Game game) {
        Data.getData().getItems().stream()
                .filter(itemInfo -> itemInfo.isConsumable)
                .forEach(itemInfo -> listConsumables.addView(UILabel.create(null)
                        .setText(itemInfo.label)
                        .setSize(100, 22)
                        .setOnClickListener((GameEvent event) -> {
                            Log.info("select consumable: " + itemInfo.name);
                            _consumable = itemInfo;
                        })));

        _worldInteraction.addObserver(new WorldInteractionModuleObserver() {
            @Override
            public void onSelect(GameEvent event, Collection<ParcelModel> parcels) {
                if (_consumable != null) {
                    Log.info("put consumable on map: " + _consumable.name);
                    parcels.forEach(parcel -> {
                        _consumableModule.create(_consumable, 100, parcel);
                        _consumable = null;
                    });
                }
            }
        });
    }
}
