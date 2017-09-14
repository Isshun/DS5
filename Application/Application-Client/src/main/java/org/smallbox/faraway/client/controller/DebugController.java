//package org.smallbox.faraway.client.controller;
//
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.graphics.Color;
//import org.smallbox.faraway.client.controller.annotation.BindLua;
//import org.smallbox.faraway.client.controller.annotation.BindLuaController;
//import org.smallbox.faraway.client.controller.character.CharacterInfoController;
//import org.smallbox.faraway.client.render.LayerManager;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UICheckBox;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
//import org.smallbox.faraway.core.GameShortcut;
//import org.smallbox.faraway.common.dependencyInjector.BindComponent;
//import org.smallbox.faraway.common.dependencyInjector.GameObject;
//import org.smallbox.faraway.core.engine.ColorUtils;
//import org.smallbox.faraway.modules.character.CharacterModule;
//import org.smallbox.faraway.modules.consumable.ConsumableModule;
//
///**
// * Created by Alex on 25/07/2016.
// */
//@GameObject
//public class DebugController extends LuaController {
//
//    @BindLua
//    private UIList listDebug;
//
////    @BindComponent
////    private CharacterModule characterModule;
//
////    @BindComponent
////    private ConsumableModule consumableModule;
//
//    @BindLuaController
//    private MainPanelController mainPanelController;
//
//    @BindLuaController
//    private CharacterInfoController characterInfoController;
//
//    @BindComponent
//    private LayerManager layerManager;
//
//    @Override
//    public void onReloadUI() {
//        mainPanelController.addShortcut("Debug", this);
//
//        listDebug.addView(UILabel.create(null).setText("Add character").setBackgroundColor(Color.BLUE).setSize(200, 20).setOnClickListener((int x, int y) -> characterModule.addRandom()));
//        listDebug.addView(UILabel.create(null).setText("Add rice").setBackgroundColor(Color.BLUE).setSize(200, 20).setOnClickListener((int x, int y) -> consumableModule.addConsumable("base.consumable.vegetable.rice", 10, 5, 5, 1)));
//
//        layerManager.getLayers().forEach(layer ->
//                listDebug.addView(UICheckBox.create(null)
//                        .setText("Layer: " + layer.getClass().getSimpleName())
//                        .setTextColor(ColorUtils.COLOR2)
//                        .setOnCheckListener((checked, clickOnBox) ->layer.setVisibility(checked))
//                        .setChecked(layer.isVisible())
//                        .setSize(200, 22)));
//    }
//
//    @GameShortcut(key = Input.Keys.D)
//    public void onPressT() {
//        setVisible(true);
//    }
//
//}
