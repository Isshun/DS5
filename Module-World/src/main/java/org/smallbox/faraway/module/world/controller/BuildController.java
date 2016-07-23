package org.smallbox.faraway.module.world.controller;

import org.smallbox.faraway.core.engine.Color;
import org.smallbox.faraway.core.engine.renderer.SpriteManager;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.BindLuaAction;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.module.world.UIBuildModule;
import org.smallbox.faraway.ui.engine.views.widgets.*;

import java.util.Arrays;

/**
 * Created by Alex on 22/07/2016.
 */
public class BuildController extends LuaController {
    @BindLua
    private UIFrame content;

    @BindLua
    private UILabel contentLabel;

    private UIBuildModule _uiBuildModule;
    private ItemInfo _structureInfo;

    @Override
    protected void onCreate() {
    }

    public void create(UIBuildModule uiBuildModule) {
        _uiBuildModule = uiBuildModule;
        onOpenItems(null);
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

        Data.getData().getItems().stream()
                .filter(item -> item.isUserItem)
                .forEach(item -> {
                    UIFrame frame = new UIFrame(null);
                    frame.setSize(300, 32);
                    frame.setOnClickListener(() -> _uiBuildModule.selectItem(item));

                    UIImage image = new UIImage(null);
                    image.setImage(SpriteManager.getInstance().getIcon(item));
                    frame.addView(image);

                    UILabel label = new UILabel(null);
                    label.setMargin(14, 0, 0, 38);
                    label.setText(item.label);
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
        for (String parentName: Arrays.asList("base.wall", "base.door", "base.floor")) {
            structureList.addView(UILabel.create(null)
                    .setText(Data.getData().getItemInfo(parentName).label)
                    .setSize(100, 32)
                    .setBackgroundFocusColor(0x25c9cb)
                    .setPadding(10)
                    .setOnClickListener(() -> {
                        materialList.clear();
                        for (ItemInfo itemInfo: Data.getData().items) {
                            if (itemInfo.receipts != null && !itemInfo.receipts.isEmpty() && parentName.equals(itemInfo.parentName)) {
                                UIFrame viewMaterial = new UIFrame(null);
                                viewMaterial.setSize(200, 32);

                                View icMaterial = UIImage.create(null).setImage(itemInfo.receipts.get(0).icon).setSize(32, 32);
                                icMaterial.setOnClickListener(() -> selectStructure(icMaterial, itemInfo));
                                viewMaterial.addView(icMaterial);

                                viewMaterial.addView(UILabel.create(null).setText(itemInfo.receipts.get(0).label).setSize(100, 22).setPadding(5).setPosition(32, 0));

                                materialList.addView(viewMaterial);
                            }
                            if (itemInfo == _structureInfo) {
//                                listEntry.setBackgroundColor(0x349394);
                            }
                        }
                    }));
        }
        content.addView(materialList);

//            UIFrame listEntry = new UIFrame(null);
//            listEntry.setSize(372, 32);
//            listEntry.addView(UILabel.create(null).setText(Data.getData().getItemInfo(parentName).label).setSize(372, 32).setPadding(10));
//            UIGrid categoryGrid = new UIGrid(null);
//            categoryGrid.setSize(200, 32);
//            categoryGrid.setPosition(200, 0);
//            categoryGrid.setColumns(10);
//            categoryGrid.setColumnWidth(32);
//            categoryGrid.setRowHeight(32);
//            for (ItemInfo itemInfo: Data.getData().items) {
//                if (itemInfo.receipts != null && !itemInfo.receipts.isEmpty() && parentName.equals(itemInfo.parentName)) {
//                    View icStructure = UIImage.create(null).setImage(itemInfo.receipts.get(0).icon).setSize(32, 32);
//                    icStructure.setOnClickListener(() -> selectStructure(icStructure, itemInfo));
//                    categoryGrid.addView(icStructure);
//                }
//                if (itemInfo == _structureInfo) {
//                    listEntry.setBackgroundColor(0x349394);
//                }
//            }
//            listEntry.addView(categoryGrid);
//            itemGrid.addView(listEntry);
//        }
    }

    private void selectStructure(View view, ItemInfo structureInfo) {
        _structureInfo = structureInfo;
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

        Data.getData().getItems().stream()
                .filter(item -> item.isNetworkItem)
                .forEach(item -> {
                    UIFrame frame = new UIFrame(null);
                    frame.setSize(300, 32);
                    frame.setOnClickListener(() -> _uiBuildModule.selectItem(item));

                    UIImage image = new UIImage(null);
                    image.setImage(SpriteManager.getInstance().getIcon(item));
                    frame.addView(image);

                    UILabel label = new UILabel(null);
                    label.setMargin(14, 0, 0, 38);
                    label.setText(item.label);
                    frame.addView(label);

                    itemGrid.addView(frame);
                });
    }
}
