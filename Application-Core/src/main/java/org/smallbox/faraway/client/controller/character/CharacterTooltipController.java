//package org.smallbox.faraway.client.controller.character;
//
//import org.smallbox.faraway.client.controller.LuaController;
//import org.smallbox.faraway.client.controller.TooltipController;
//import org.smallbox.faraway.client.controller.annotation.BindLua;
//import org.smallbox.faraway.client.renderer.Viewport;
//import org.smallbox.faraway.client.ui.widgets.UILabel;
//import org.smallbox.faraway.client.ui.widgets.View;
//import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
//import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
//import org.smallbox.faraway.core.world.model.ParcelModel;
//import org.smallbox.faraway.modules.character.CharacterModule;
//import org.smallbox.faraway.modules.character.model.base.CharacterModel;
//import org.smallbox.faraway.modules.world.WorldModule;
//
//@GameObject
//public class CharacterTooltipController extends LuaController {
//
//    @BindLua private UILabel lbName;
//    @BindLua private View content;
//
//    @Inject
//    private WorldModule worldModule;
//
//    @Inject
//    private CharacterModule characterModule;
//
//    @Inject
//    private Viewport viewport;
//
//    @Inject
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
