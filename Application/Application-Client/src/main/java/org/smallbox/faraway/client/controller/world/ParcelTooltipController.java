package org.smallbox.faraway.client.controller.world;

import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.TooltipController;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 26/04/2016.
 */
public class ParcelTooltipController extends LuaController {

    @BindLua private UILabel lbPosition;
    @BindLua private UILabel lbGroundInfo;
    @BindLua private UILabel lbRockInfo;
    @BindLua private View content;

    @BindModule
    private WorldModule worldModule;

    @BindComponent
    private Viewport viewport;

    @BindLuaController
    private TooltipController tooltipController;

    @Override
    public void onMouseMove(GameEvent event) {
        int worldX = viewport.getWorldPosX(event.mouseEvent.x);
        int worldY = viewport.getWorldPosY(event.mouseEvent.y);
        int worldZ = viewport.getFloor();

        ParcelModel parcel = worldModule.getParcel(worldX, worldY, worldZ);

        // Display parcel information
        if (parcel != null) {
            lbPosition.setText(worldX + " x " + worldY + " x " + worldZ);
            lbGroundInfo.setText(parcel.getGroundInfo() != null ? parcel.getGroundInfo().label : null);
            lbRockInfo.setText(parcel.getRockInfo() != null ? parcel.getRockInfo().label : null);

            tooltipController.addSubView("parcel", getRootView());
        }

        // No parcel - hide view
        else {
            tooltipController.removeSubView("parcel");
        }

    }

    @Override
    protected void onControllerUpdate() {

    }
}
