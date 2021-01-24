package org.smallbox.faraway.module.dev.controller.info;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.world.model.ParcelModel;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;

public class DebugParcelInfoController extends LuaController {

    @BindModule
    private WorldModule worldModule;

    @BindLua private UILabel    lbName;
    @BindLua private UILabel    lbGround;
    @BindLua private UILabel    lbTile;
    @BindLua private UILabel    lbPosition;
    @BindLua private UILabel    lbConnections;
    @BindLua private UILabel    lbType;
    @BindLua private UILabel    lbOxygen;
    @BindLua private UILabel    lbWater;
    @BindLua private UILabel    lbLight;
    @BindLua private UILabel    lbTemperature;
    @BindLua private UILabel    lbWalkable;
    @BindLua private UILabel    lbRoom;
    @BindLua private UILabel    lbRock;
    @BindLua private UILabel    lbStructure;
    @BindLua private UILabel    lbConsumable;
    @BindLua private UILabel    lbItem;
    @BindLua private UILabel    lbPlant;
    @BindLua private UILabel    lbNetwork;

    @Override
    public void onGameCreate(Game game) {
        worldModule.addObserver(new WorldModuleObserver() {
            @Override
            public void onOverParcel(ParcelModel parcel) {
                selectParcel(parcel);
            }
        });
    }

    private void selectParcel(ParcelModel parcel) {
        lbPosition.setText(parcel.x + "x" + parcel.y + "x" + parcel.z);
    }
}
