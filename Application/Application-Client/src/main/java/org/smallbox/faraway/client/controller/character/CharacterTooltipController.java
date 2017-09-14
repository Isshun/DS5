//package org.smallbox.faraway.client.controller.character;
//
//import org.smallbox.faraway.client.controller.LuaController;
//import org.smallbox.faraway.client.controller.TooltipController;
//import org.smallbox.faraway.client.controller.annotation.BindLua;
//import org.smallbox.faraway.client.controller.annotation.BindLuaController;
//import org.smallbox.faraway.client.render.Viewport;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.client.ui.engine.views.widgets.View;
//import org.smallbox.faraway.common.dependencyInjector.BindComponent;
//import org.smallbox.faraway.common.dependencyInjector.GameObject;
//import org.smallbox.faraway.core.module.world.model.ParcelModel;
//import org.smallbox.faraway.modules.character.CharacterModule;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.world.WorldModule;
//
///**
// * Created by Alex on 26/04/2016.
// */
//@GameObject
//public class CharacterTooltipController extends LuaController {
//
//    @BindLua private UILabel lbName;
//    @BindLua private View content;
//
//    @BindComponent
//    private WorldModule worldModule;
//
//    @BindComponent
//    private CharacterModule characterModule;
//
//    @BindComponent
//    private Viewport viewport;
//
//    @BindLuaController
//    private TooltipController tooltipController;
//
//    @Override
//    public void onMouseMove(int x, int y, int button) {
//        tooltipController.removeSubView("character");
//
//        int worldX = viewport.getWorldPosX(x);
//        int worldY = viewport.getWorldPosY(y);
//        int worldZ = viewport.getFloor();
//
//        ParcelModel parcel = worldModule.getParcel(worldX, worldY, worldZ);
//
//        // Display parcel information
//        if (parcel != null) {
//
//            CharacterModel character = characterModule.getCharacter(parcel);
//
//            if (character != null) {
//                lbName.setText(character.getName());
//                tooltipController.addSubView("character", getRootView());
//            }
//
//        }
//
//    }
//
//}
