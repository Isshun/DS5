package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.renderer.BuildRenderer;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 22/07/2016.
 */
public class BuildController extends LuaController {

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
    private BuildRenderer buildRenderer;

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
//        buildRenderer.setItemInfo(null);
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

        Application.data.getItems().stream()
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

        Application.data.getItems().stream()
                .filter(itemInfo -> itemInfo.isStructure)
                .forEach(itemInfo -> listStructures.addNextView(UILabel.create(null)
                        .setText(itemInfo.label)
                        .setTextColor(0x9afbff)
                        .setSize(100, 32)
                        .setBackgroundFocusColor(0x25c9cb)
                        .setPadding(10)
                        .setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo))));
//                    .setOnClickListener(() -> {
//                        materialList.removeAllViews();
//                        for (ItemInfo itemInfo: Application.data.items) {
//                            if (itemInfo.receiptGroups != null && !itemInfo.receiptGroups.isEmpty() && parentName.equals(itemInfo.parentName)) {
//                                UIFrame viewMaterial = new UIFrame(null);
//                                viewMaterial.setSize(200, 32);
//
//                                View icMaterial = UIImage.createGame(null).setImage(itemInfo.receiptGroups.get(0).icon).setSize(32, 32);
//                                icMaterial.setOnClickListener(() -> _currentItem = itemInfo);
//                                viewMaterial.addView(icMaterial);
//
//                                viewMaterial.addView(UILabel.createGame(null).setText(itemInfo.receiptGroups.get(0).label).setSize(100, 22).setPadding(5).setPosition(32, 0));
//
//                                materialList.addView(viewMaterial);
//                            }
//                            if (itemInfo == _currentItem) {
////                                listEntry.setBackgroundColor(0x349394);
//                            }
//                        }
//                    }));

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
            buildRenderer.setItemInfo(itemInfo);

            uiEventManager.setSelectionListener(parcels -> {
                if (_currentItem != null) {
                    if (_currentItem.isUserItem) {
                        parcels.forEach(parcel -> itemModule.addPattern(parcel, _currentItem));
                    }
                    if (_currentItem.isStructure) {
                        parcels.forEach(parcel -> structureModule.addPattern(parcel, _currentItem));
                    }
                }

                buildRenderer.setItemInfo(_currentItem);
                return false;
            });

//            worldInteractionModule.setOnClickListener(parcelList -> {
//                parcelList.forEach(parcel -> {
//                    if (itemInfo.isUserItem) {
//                        itemModule.addPattern(parcel, itemInfo);
//                    }
//                    if (itemInfo.isStructure) {
//                        structureModule.addPattern(parcel, itemInfo);
//                    }
//                });
//                _currentItem = null;
//                return true;
//            });
        }
    }

    public ItemInfo getCurrentItem() {
        return _currentItem;
    }

    @Override
    protected void onControllerUpdate() {

    }
}
