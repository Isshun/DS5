package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 26/04/2016.
 */
public class ParcelInfoController extends LuaController {

    @BindLua private UILabel lbPosition;
    @BindLua private UILabel lbGroundInfo;
    @BindLua private UILabel lbRockInfo;

    @BindModule
    private WorldModule worldModule;

    @BindComponent
    private Viewport viewport;

    @Override
    public void onMouseMove(GameEvent event) {
        int worldX = viewport.getWorldPosX(event.mouseEvent.x);
        int worldY = viewport.getWorldPosY(event.mouseEvent.y);
        int worldZ = viewport.getFloor();

        ParcelModel parcel = worldModule.getParcel(worldX, worldY, worldZ);

        // Display parcel information
        if (parcel != null) {
            setVisible(true);

            lbPosition.setText(worldX + " x " + worldY + " x " + worldZ);
            lbGroundInfo.setText(parcel.getGroundInfo() != null ? parcel.getGroundInfo().label : null);
            lbRockInfo.setText(parcel.getRockInfo() != null ? parcel.getRockInfo().label : null);
        }

        // No parcel - hide view
        else {
            setVisible(false);
        }

    }
}