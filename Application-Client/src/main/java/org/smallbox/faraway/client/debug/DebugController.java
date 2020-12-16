//package org.smallbox.faraway.client.debug;
//
//import org.smallbox.faraway.client.ApplicationClient;
//import org.smallbox.faraway.client.controller.BindLuaController;
//import org.smallbox.faraway.client.controller.LuaController;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UICheckBox;
//import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
//import org.smallbox.faraway.core.config.Config;
//import org.smallbox.faraway.core.dependencyInjector.BindModule;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.lua.BindLua;
//import org.smallbox.faraway.module.consumable.ConsumableModule;
//import org.smallbox.faraway.module.item.ItemModule;
//import org.smallbox.faraway.module.mainPanel.MainPanelController;
//import org.smallbox.faraway.module.structure.StructureModule;
//
///**
// * Created by Alex on 03/12/2016.
// */
//public class DebugController extends LuaController {
//
//    @BindLua
//    private UICheckBox cbViewId;
//
//    @BindLua
//    private UICheckBox cbViewWindow;
//
//    @BindLua
//    private UICheckBox cbRenderWindow;
//
//    @BindLua
//    private UILabel lbCount;
//
//    @BindLuaController
//    private MainPanelController mainPanelController;
//
//    @Inject
//    private ItemModule itemModule;
//
//    @Inject
//    private StructureModule structureModule;
//
//    @Inject
//    private ConsumableModule consumableModule;
//
//    @Override
//    public void onReloadUI() {
//        mainPanelController.addShortcut("Debug", this);
//
//        cbViewId.setOnCheckListener(checked -> Config.onDebugView = checked == UICheckBox.Value.TRUE);
//        cbViewWindow.setOnCheckListener(checked -> ApplicationClient.uiManager.findById("base.ui.info_views").setVisible(checked == UICheckBox.Value.TRUE));
//        cbRenderWindow.setOnCheckListener(checked -> ApplicationClient.uiManager.findById("base.ui.debug.renders_list").setVisible(checked == UICheckBox.Value.TRUE));
//    }
//
//    @Override
//    public void onGameUpdate(Game game) {
//        lbCount.setText(String.format("items: %d, structures: %d, consumables: %d",
//                itemModule.getItems().size(),
//                structureModule.getStructures().size(),
//                consumableModule.getConsumables().size()));
//    }
//}
