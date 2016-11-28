package org.smallbox.faraway.module.dev.controller.info;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
import org.smallbox.faraway.module.world.WorldModule;
import org.smallbox.faraway.module.world.WorldModuleObserver;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 31/07/2016.
 */
public class DebugParcelInfoController extends LuaController {
    @BindModule
    private WorldModule _world;

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
    protected void onGameCreate(Game game) {
        _world.addObserver(new WorldModuleObserver() {
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
