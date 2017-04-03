package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.render.layer.BuildLayer;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 22/07/2016.
 */
public class BuildController extends LuaController {

    @BindComponent
    private Data data;

    @BindComponent
    private UIEventManager uiEventManager;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private ItemModule itemModule;

    @BindModule
    private StructureModule structureModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindLua
    private UIList listStructures;

    @BindLua
    private UIList listItems;

    @BindLua
    private UIList listNetworks;

    @BindLua
    private UILabel contentLabel;

    @BindComponent
    private BuildLayer buildLayer;

    private ItemInfo _currentItem;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Build", this);
    }
//
//    @Override
//    public boolean onClickOnParcel(List<ParcelModel> parcels) {
////        if (_currentItem != null) {
////            if (_currentItem.isUserItem) {
////                parcels.forEach(parcel -> itemModule.addPattern(parcel, _currentItem));
////            }
////            if (_currentItem.isStructure) {
////                parcels.forEach(parcel -> structureModule.addPattern(parcel, _currentItem));
////            }
////        }
//
//        _currentItem = null;
//
//        buildLayer.setItemInfo(null);
//
//        return false;
//    }

    @Override
    public boolean onKeyPress(int key) {
        if (key == Input.Keys.ESCAPE && _currentItem != null) {
            _currentItem = null;
            return true;
        }
        return false;
    }

//    @Override
//    public void onMouseRelease(GameEvent event) {
//        if (event.mouseEvent != null && event.mouseEvent.button == GameEventListener.MouseButton.RIGHT) {
//            _currentItem = null;
//        }
//    }

    @BindLuaAction
    private void onOpenItems(View view) {
        listItems.setVisible(!listItems.isVisible());
        listStructures.setVisible(false);
        listNetworks.setVisible(false);

        data.getItems().stream()
                .filter(item -> item.isUserItem)
                .forEach(itemInfo -> {
                    UIFrame frame = new UIFrame(null);
                    frame.setSize(300, 32);
                    frame.setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo));
                    frame.setBackgroundFocusColor(0xffff2233);

                    UIImage image = new UIImage(null);
                    image.setImage(ApplicationClient.spriteManager.getIcon(itemInfo));
                    frame.addView(image);

                    UILabel label = new UILabel(null);
                    label.setTextColor(0x9afbff);
                    label.setMargin(14, 0, 0, 38);
                    label.setText(itemInfo.label);
                    frame.addView(label);

                    listItems.addNextView(frame);
                });
        listItems.setHeight(listItems.getViews().size() * 32);
        listItems.switchViews();
    }

    @BindLuaAction
    private void onOpenStructures(View view) {
        listItems.setVisible(false);
        listStructures.setVisible(!listStructures.isVisible());
        listNetworks.setVisible(false);

        data.getItems().stream()
                .filter(itemInfo -> itemInfo.isStructure)
                .forEach(itemInfo -> listStructures.addNextView(UILabel.create(null)
                        .setText(itemInfo.label)
                        .setTextColor(0x9afbff)
                        .setSize(100, 32)
                        .setBackgroundFocusColor(0xff25c9cb)
                        .setPadding(10)
                        .setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo))));

        listStructures.setHeight(listStructures.getViews().size() * 32);
        listStructures.switchViews();
    }

    @BindLuaAction
    private void onOpenNetworks(View view) {
        listItems.setVisible(false);
        listStructures.setVisible(false);
        listNetworks.setVisible(!listNetworks.isVisible());

        Application.data.getItems().stream()
                .filter(item -> item.isNetworkItem)
                .forEach(itemInfo -> {
                    UIFrame frame = new UIFrame(null);
                    frame.setSize(300, 32);
                    frame.setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo));

                    UIImage image = new UIImage(null);
                    image.setImage(ApplicationClient.spriteManager.getIcon(itemInfo));
                    frame.addView(image);

                    UILabel label = new UILabel(null);
                    label.setMargin(14, 0, 0, 38);
                    label.setText(itemInfo.label);
                    label.setTextColor(0x9afbff);
                    frame.addView(label);

                    listNetworks.addNextView(frame);
                });
        listNetworks.setHeight(listNetworks.getViews().size() * 32);
        listNetworks.switchViews();
    }

    public void setCurrentItem(ItemInfo itemInfo) {
        _currentItem = itemInfo;

        if (itemInfo != null) {
            buildLayer.setItemInfo(itemInfo);

            uiEventManager.setSelectionListener(parcels -> {
                if (_currentItem != null) {
                    if (_currentItem.isUserItem) {
                        parcels.forEach(parcel -> itemModule.addPattern(parcel, _currentItem));
                    }
                    if (_currentItem.isStructure) {
                        parcels.forEach(parcel -> structureModule.addPattern(parcel, _currentItem));
                    }
                }

                buildLayer.setItemInfo(_currentItem);
                return false;
            });

        }
    }

    public ItemInfo getCurrentItem() {
        return _currentItem;
    }

}
