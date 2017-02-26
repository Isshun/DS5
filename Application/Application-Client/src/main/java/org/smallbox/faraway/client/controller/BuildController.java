package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.WorldInteractionModule;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.Application;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.GameEventListener;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.lua.BindLuaAction;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.List;

/**
 * Created by Alex on 22/07/2016.
 */
public class BuildController extends LuaController {

    @BindModule
    private WorldInteractionModule worldInteractionModule;

    @BindModule
    private WorldModule worldModule;

    @BindModule
    private ItemModule itemModule;

    @BindModule
    private StructureModule structureModule;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindLua
    private UIFrame content;

    @BindLua
    private UILabel contentLabel;

    private ItemInfo _currentItem;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Build", this);
    }

    @Override
    public boolean onClickOnParcel(List<ParcelModel> parcels) {
        if (_currentItem != null) {
            if (_currentItem.isUserItem) {
                parcels.forEach(parcel -> itemModule.addPattern(parcel, _currentItem));
            }
            if (_currentItem.isStructure) {
                parcels.forEach(parcel -> structureModule.addPattern(parcel, _currentItem));
            }
        }

        _currentItem = null;

        return false;
    }

    @Override
    public boolean onKeyPress(GameEventListener.Key key) {
        if (key == GameEventListener.Key.ESCAPE && _currentItem != null) {
            _currentItem = null;
            return true;
        }
        return false;
    }

    @Override
    public void onMouseRelease(GameEvent event) {
        if (event.mouseEvent != null && event.mouseEvent.button == GameEventListener.MouseButton.RIGHT) {
            _currentItem = null;
        }
    }

    @BindLuaAction
    private void onOpenItems(View view) {
        contentLabel.setText("Build Items");

        content.clear();

        UIGrid itemGrid = new UIGrid(null);
        itemGrid.setColumns(1);
        itemGrid.setColumnWidth(300);
        itemGrid.setRowHeight(32);
        content.addView(itemGrid);

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
                    label.setMargin(14, 0, 0, 38);
                    label.setText(itemInfo.label);
                    frame.addView(label);

                    itemGrid.addView(frame);
                });
    }

    @BindLuaAction
    private void onOpenStructures(View view) {
        contentLabel.setText("Build Structures");

        content.clear();

        UIList materialList = new UIList(null);
        materialList.setPosition(100, 0);

        UIList structureList = new UIList(null);
        content.addView(structureList);
        Application.data.getItems().stream()
                .filter(itemInfo -> itemInfo.isStructure)
                .forEach(itemInfo -> structureList.addView(UILabel.create(null)
                        .setText(itemInfo.label)
                        .setSize(100, 32)
                        .setBackgroundFocusColor(0x25c9cb)
                        .setPadding(10)
                        .setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo))));
//                    .setOnClickListener(() -> {
//                        materialList.clear();
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
        content.addView(materialList);
    }

    @BindLuaAction
    private void onOpenNetworks(View view) {
        contentLabel.setText("Build Networks");

        content.clear();

        UIGrid itemGrid = new UIGrid(null);
        itemGrid.setColumns(1);
        itemGrid.setColumnWidth(300);
        itemGrid.setRowHeight(32);
        content.addView(itemGrid);

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
                    frame.addView(label);

                    itemGrid.addView(frame);
                });
    }

    public void setCurrentItem(ItemInfo itemInfo) {
        _currentItem = itemInfo;

        if (itemInfo != null) {
            worldInteractionModule.setOnClickListener(parcelList -> {
                parcelList.forEach(parcel -> {
                    if (itemInfo.isUserItem) {
                        itemModule.addPattern(parcel, itemInfo);
                    }
                    if (itemInfo.isStructure) {
                        structureModule.addPattern(parcel, itemInfo);
                    }
                });
                _currentItem = null;
                return true;
            });
        }
    }

    public ItemInfo getCurrentItem() {
        return _currentItem;
    }

    @Override
    protected void onNewGameUpdate(Game game) {

    }
}
