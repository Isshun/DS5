package org.smallbox.faraway.client.controller.world;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.TooltipController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 26/04/2016.
 */
@GameObject
public class ParcelTooltipController extends LuaController {

    @BindLua private UILabel lbPosition;
    @BindLua private UILabel lbGroundInfo;
    @BindLua private UILabel lbRockInfo;
    @BindLua private View content;

    @Inject
    private WorldModule worldModule;

    @Inject
    private Viewport viewport;

    @Inject
    private TooltipController tooltipController;

    @Override
    public void onMouseMove(int x, int y, int button) {
        int worldX = viewport.getWorldPosX(x);
        int worldY = viewport.getWorldPosY(y);
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

}
