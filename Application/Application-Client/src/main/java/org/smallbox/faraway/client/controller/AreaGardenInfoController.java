//package org.smallbox.faraway.client.controller;
//
//import org.smallbox.faraway.GameEvent;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
//import org.smallbox.faraway.core.lua.BindLua;
//import org.smallbox.faraway.core.module.area.model.GardenAreaModel;
//
///**
// * Created by Alex on 26/04/2016.
// */
//public class AreaGardenInfoController extends LuaController {
//    @BindLua private UIList             listAcceptedPlant;
//
//    public void select(GardenAreaModel garden) {
//        setVisible(true);
//
////        ApplicationClient.uiManager.findById("base.ui.panel_main").setVisible(false);
////        ApplicationClient.uiManager.findById("base.ui.panel_areas").setVisible(false);
//
//        displayAcceptedItem(garden);
//    }
//
//    private void displayAcceptedItem(GardenAreaModel garden) {
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
