//package org.smallbox.faraway.client.controller;
//
//import org.smallbox.faraway.GameEvent;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
//import org.smallbox.faraway.core.lua.BindLua;
//import org.smallbox.faraway.core.module.area.model.GardenArea;
//
///**
// * Created by Alex on 26/04/2016.
// */
//public class AreaInfoGardenController extends LuaController {
//    @BindLua private UIList             listAcceptedPlant;
//
//    public void select(GardenArea garden) {
//        setVisible(true);
//
////        ApplicationClient.uiManager.findById("base.ui.right_panel").setVisible(false);
////        ApplicationClient.uiManager.findById("base.ui.panel_areas").setVisible(false);
//
//        displayAcceptedItem(garden);
//    }
//
//    private void displayAcceptedItem(GardenArea garden) {
//        listAcceptedPlant.clear();
//        garden.getPotentials().forEach(itemInfo -> {
//            UILabel label = new UILabel(null);
//            label.setText((garden.getCurrent() == itemInfo ? "[x] " : "[ ] ") + itemInfo.label);
//            label.setTextSize(12);
//            label.setPadding(5);
//            label.setOnClickListener((GameEvent event) -> {
//                garden.setAccept(itemInfo, true);
//            });
//            listAcceptedPlant.addView(label);
//        });
//    }
//}
