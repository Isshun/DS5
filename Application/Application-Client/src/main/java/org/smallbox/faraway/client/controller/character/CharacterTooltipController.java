package org.smallbox.faraway.client.controller.character;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.controller.BindLuaController;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.TooltipController;
import org.smallbox.faraway.client.renderer.Viewport;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.modules.character.model.base.CharacterModel;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.character.CharacterModule;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 26/04/2016.
 */
public class CharacterTooltipController extends LuaController {

    @BindLua private UILabel lbName;
    @BindLua private View content;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private CharacterModule characterModule;

    @BindComponent
    private Viewport viewport;

    @BindLuaController
    private TooltipController tooltipController;

    @Override
    public void onMouseMove(GameEvent event) {
        tooltipController.removeSubView("character");

        int worldX = viewport.getWorldPosX(event.mouseEvent.x);
        int worldY = viewport.getWorldPosY(event.mouseEvent.y);
        int worldZ = viewport.getFloor();

        ParcelModel parcel = worldModule.getParcel(worldX, worldY, worldZ);

        // Display parcel information
        if (parcel != null) {

            CharacterModel character = characterModule.getCharacter(parcel);

            if (character != null) {
                lbName.setText(character.getName());
                tooltipController.addSubView("character", getRootView());
            }

        }

    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}