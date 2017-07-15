package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.render.layer.BuildLayer;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.GameShortcut;
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
    private UIList list;

    @BindLua
    private UILabel contentLabel;

    @BindComponent
    private BuildLayer buildLayer;

    private ItemInfo _currentItem;

    @Override
    public void onReloadUI() {
        mainPanelController.addShortcut("Build", this);
        onOpenItems();
    }

//    @Override
//    public boolean onKeyPress(int key) {
//        if (key == Input.Keys.ESCAPE && _currentItem != null) {
//            _currentItem = null;
//            return true;
//        }
//        return false;
//    }

    private void onOpenItems() {
        list.removeAllViews();
        list.addView(createListTitle("Items", event -> onOpenItems()));

        data.getItems().stream()
                .filter(item -> item.isUserItem)
                .forEach(itemInfo -> {
                    UIFrame frame = new UIFrame(null);
                    frame.setSize(300, 32);
                    frame.setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo));
                    frame.setBackgroundFocusColor(0xff2233ff);

                    UIImage image = new UIImage(null);
                    image.setImage(ApplicationClient.spriteManager.getIcon(itemInfo));
                    frame.addView(image);

                    UILabel label = new UILabel(null);
                    label.setTextColor(0x9afbffff);
                    label.setMargin(14, 0, 0, 38);
                    label.setText(itemInfo.label);
                    frame.addView(label);

                    list.addView(frame);
                });

        list.addView(createListTitle("Structure", event -> onOpenStructures()));
        list.addView(createListTitle("Network", event -> onOpenNetworks()));
    }

    private void onOpenStructures() {
        list.removeAllViews();
        list.addView(createListTitle("Items", event -> onOpenItems()));
        list.addView(createListTitle("Structure", event -> onOpenStructures()));

        data.getItems().stream()
                .filter(itemInfo -> itemInfo.isStructure)
                .forEach(itemInfo -> list.addView(UILabel.create(null)
                        .setText(itemInfo.label)
                        .setTextColor(0x9afbffff)
                        .setSize(100, 32)
                        .setBackgroundFocusColor(0x25c9cbff)
                        .setPadding(10)
                        .setOnClickListener((GameEvent event) -> setCurrentItem(itemInfo))));

        list.addView(createListTitle("Network", event -> onOpenNetworks()));
    }

    private void onOpenNetworks() {
        list.removeAllViews();
        list.addView(createListTitle("Items", event -> onOpenItems()));
        list.addView(createListTitle("Structure", event -> onOpenStructures()));
        list.addView(createListTitle("Network", event -> onOpenNetworks()));

        data.getItems().stream()
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
                    label.setTextColor(0x9afbffff);
                    frame.addView(label);

                    list.addView(frame);
                });
    }

    private View createListTitle(String label, OnClickListener clickListener) {
        return UILabel.create(null)
                .setText(label)
                .setTextSize(16)
                .setPadding(5)
                .setSize(350, 28)
                .setBackgroundColor(0x349394ff)
                .setFocusBackgroundColor(0x25c9cbff)
                .setOnClickListener(clickListener);
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

    @GameShortcut(key = Input.Keys.B)
    public void onPressT() {
        setVisible(true);
    }
}
