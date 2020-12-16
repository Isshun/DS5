package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.ApplicationClient;
import org.smallbox.faraway.client.SelectionManager;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.render.layer.BuildLayer;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.AfterGameLayerInit;
import org.smallbox.faraway.core.dependencyInjector.Inject;
import org.smallbox.faraway.core.dependencyInjector.GameObject;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;

/**
 * Created by Alex on 22/07/2016.
 */
@GameObject
public class BuildController extends LuaController {

    @Inject
    protected SelectionManager selectionManager;

    @Inject
    private Data data;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private WorldModule worldModule;

    @Inject
    private ItemModule itemModule;

    @Inject
    private StructureModule structureModule;

    @Inject
    private MainPanelController mainPanelController;

    @BindLua
    private UIList list;

    @BindLua
    private UILabel contentLabel;

    @Inject
    private BuildLayer buildLayer;

    private ItemInfo _currentItem;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
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
        list.addView(createListTitle("Items", (int x, int y) -> onOpenItems()));

        data.getItems().stream()
                .filter(item -> item.isUserItem)
                .forEach(itemInfo ->
                        list.addView(new UIFrame(null)
                                .setSize(300, 32)
                                .setOnClickListener((int x, int y) -> setCurrentItem(itemInfo))
                                .setBackgroundFocusColor(0xff2233ff)
                                .addView(UIImage.create(null)
                                        .setImage(ApplicationClient.spriteManager.getIcon(itemInfo)))
                                .addView(UILabel.create(null)
                                        .setText(itemInfo.label)
                                        .setTextColor(0x9afbffff)
                                        .setMargin(14, 0, 0, 38))));

        list.addView(createListTitle("Structure", (int x, int y) -> onOpenStructures()));
        list.addView(createListTitle("Network", (int x, int y) -> onOpenNetworks()));
    }

    private void onOpenStructures() {
        list.removeAllViews();
        list.addView(createListTitle("Items", (int x, int y) -> onOpenItems()));
        list.addView(createListTitle("Structure", (int x, int y) -> onOpenStructures()));

        data.getItems().stream()
                .filter(itemInfo -> itemInfo.isStructure)
                .forEach(itemInfo ->
                        list.addView(new UIFrame(null)
                                .setSize(300, 32)
                                .setOnClickListener((int x, int y) -> setCurrentItem(itemInfo))
                                .setBackgroundFocusColor(0xff2233ff)
                                .addView(UIImage.create(null)
                                        .setImage(ApplicationClient.spriteManager.getIcon(itemInfo)))
                                .addView(UILabel.create(null)
                                        .setText(itemInfo.label)
                                        .setTextColor(0x9afbffff)
                                        .setMargin(14, 0, 0, 38))));

        list.addView(createListTitle("Network", (int x, int y) -> onOpenNetworks()));
    }

    private void onOpenNetworks() {
        list.removeAllViews();
        list.addView(createListTitle("Items", (int x, int y) -> onOpenItems()));
        list.addView(createListTitle("Structure", (int x, int y) -> onOpenStructures()));
        list.addView(createListTitle("Network", (int x, int y) -> onOpenNetworks()));

        data.getItems().stream()
                .filter(item -> item.isNetworkItem)
                .forEach(itemInfo ->
                        list.addView(new UIFrame(null)
                                .setSize(300, 32)
                                .setOnClickListener((int x, int y) -> setCurrentItem(itemInfo))
                                .setBackgroundFocusColor(0xff2233ff)
                                .addView(UIImage.create(null)
                                        .setImage(ApplicationClient.spriteManager.getIcon(itemInfo)))
                                .addView(UILabel.create(null)
                                        .setText(itemInfo.label)
                                        .setTextColor(0x9afbffff)
                                        .setMargin(14, 0, 0, 38))));
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

            selectionManager.setSelectionListener(parcels -> {
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
