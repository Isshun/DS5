package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.manager.SpriteManager;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.OnClickListener;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.modules.item.ItemModule;
import org.smallbox.faraway.modules.structure.StructureModule;
import org.smallbox.faraway.modules.world.WorldModule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@GameObject
public class BuildController extends LuaController {
    @Inject protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private WorldModule worldModule;
    @Inject private ItemModule itemModule;
    @Inject private StructureModule structureModule;
    @Inject private MainPanelController mainPanelController;
    @Inject private SpriteManager spriteManager;
    @Inject private GameActionManager gameActionManager;
    @Inject private Data data;

    @BindLua private UIList listCategories;
    @BindLua private UILabel contentLabel;
    @BindLua private UILabel lbCategory;
    @BindLua private UIGrid gridItems;

    private ItemInfo _currentItem;
    private Map<String, List<ItemInfo>> itemsByCategory;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        mainPanelController.addShortcut("Build", this);

        List<String> categories = List.of("kitchen", "entertainment", "furniture", "power", "production", "other");

        // Build item map by category
        itemsByCategory = data.getItems().stream()
                .filter(itemInfo -> StringUtils.equals(itemInfo.type, "item"))
                .collect(Collectors.groupingBy(itemInfo -> categories.contains(itemInfo.category) ? itemInfo.category : "other"));

        // Add item's categories to UI
        categories.forEach(category -> {
            UIFrame viewCategory = listCategories.createFromTemplate(UIFrame.class);
            viewCategory.getEvents().setOnClickListener((x, y) -> openCategory(viewCategory, category));
            viewCategory.findImage("img_category").setImage("[base]/graphics/items/bt_categories/" + category + ".png");
            listCategories.addNextView(viewCategory);
        });
        listCategories.switchViews();

        // Open first category
        listCategories.getViews().stream().findFirst().ifPresent(view -> view.getEvents()._onClickListener.onClick(0, 0));
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
//        list.removeAllViews();
//        list.addView(createListTitle("Items", (int x, int y) -> onOpenItems()));
//
//        data.getItems().stream()
//                .filter(item -> item.isUserItem)
//                .forEach(itemInfo -> {
//                    UIFrame frame = new UIFrame(null);
//                    frame.addView(UIImage.create(null).setImage(spriteManager.getIcon(itemInfo)));
//                    frame.addView(UILabel.create(null).setText(itemInfo.label).setTextColor(0x9afbffff).getGeometry().setMargin(14, 0, 0, 38));
//                    frame.setSize(300, 32);
//                    frame.getEvents().setOnClickListener((int x, int y) -> setCurrentItem(itemInfo));
//                    frame.getStyle().setBackgroundFocusColor(0xff2233ff);
//                    list.addView(frame);
//                });
//
//        list.addView(createListTitle("Structure", (int x, int y) -> onOpenStructures()));
//        list.addView(createListTitle("Network", (int x, int y) -> onOpenNetworks()));
    }

    private void onOpenStructures() {
//        list.removeAllViews();
//        list.addView(createListTitle("Items", (int x, int y) -> onOpenItems()));
//        list.addView(createListTitle("Structure", (int x, int y) -> onOpenStructures()));
//
//        data.getItems().stream()
//                .filter(itemInfo -> itemInfo.isStructure)
//                .forEach(itemInfo -> {
//                    UIFrame frame = new UIFrame(null);
//                    frame.addView(UIImage.create(null).setImage(spriteManager.getIcon(itemInfo)));
//                    frame.addView(UILabel.create(null).setText(itemInfo.label).setTextColor(0x9afbffff).getGeometry().setMargin(14, 0, 0, 38));
//                    frame.setSize(300, 32);
//                    frame.getEvents().setOnClickListener((int x, int y) -> setCurrentItem(itemInfo));
//                    frame.getStyle().setBackgroundFocusColor(0xff2233ff);
//                    list.addView(frame);
//                });
//
//        list.addView(createListTitle("Network", (int x, int y) -> onOpenNetworks()));
    }

    private void onOpenNetworks() {
//        list.removeAllViews();
//        list.addView(createListTitle("Items", (int x, int y) -> onOpenItems()));
//        list.addView(createListTitle("Structure", (int x, int y) -> onOpenStructures()));
//        list.addView(createListTitle("Network", (int x, int y) -> onOpenNetworks()));
//
////        data.getItems().stream()
////                .filter(item -> item.isNetworkItem)
////                .forEach(itemInfo ->
////                        list.addView(new UIFrame(null)
////                                .addView(UIImage.create(null)
////                                        .setImage(spriteManager.getIcon(itemInfo)))
////                                .addView(UILabel.create(null)
////                                        .setText(itemInfo.label)
////                                        .setTextColor(0x9afbffff)
////                                        .setMargin(14, 0, 0, 38)))
////                                .setSize(300, 32)
////                                .setOnClickListener((int x, int y) -> setCurrentItem(itemInfo))
////                                .setBackgroundFocusColor(0xff2233ff)
////                );
    }

    private View createListTitle(String label, OnClickListener clickListener) {
        UILabel uiLabel = UILabel.create(null);
        uiLabel.setText(label);
        uiLabel.setTextSize(16);
        uiLabel.getGeometry().setPadding(5, 5, 5, 5);
        uiLabel.setSize(350, 28);
        uiLabel.getStyle().setBackgroundColor(0x349394ff);
        uiLabel.setFocusBackgroundColor(0x25c9cbff);
        uiLabel.getEvents().setOnClickListener(clickListener);
        return uiLabel;
    }

    public void setCurrentItem(ItemInfo itemInfo) {
        _currentItem = itemInfo;

        if (itemInfo != null) {
//            buildLayer.setItemInfo(itemInfo);
//
//            selectionManager.setSelectionListener(parcels -> {
//                if (_currentItem != null) {
//                    if (_currentItem.isUserItem) {
//                        parcels.forEach(parcel -> itemModule.addPattern(parcel, _currentItem));
//                    }
//                    if (_currentItem.isStructure) {
//                        parcels.forEach(parcel -> structureModule.addPattern(parcel, _currentItem));
//                    }
//                }
//
//                buildLayer.setItemInfo(_currentItem);
//                return false;
//            });

            gameActionManager.setBuildAction(itemInfo);
        }
    }

    public ItemInfo getCurrentItem() {
        return _currentItem;
    }

    private void openCategory(UIFrame viewCategory, String currentCategory) {
        listCategories.getViews().forEach(view -> ((CompositeView) view).findImage("img_category").getStyle().setBackgroundColor(Colors.BLUE_DARK_1));
        viewCategory.findImage("img_category").getStyle().setBackgroundColor(Colors.BLUE_LIGHT_2);

        lbCategory.setText(currentCategory);

        Optional.ofNullable(itemsByCategory.get(currentCategory)).ifPresent(itemInfos -> itemInfos.forEach(entry -> {
            UILabel lbItem = gridItems.createFromTemplate(UILabel.class);
            lbItem.setText(entry.label);
            gridItems.addNextView(lbItem);
        }));
        gridItems.switchViews();
    }

    @GameShortcut(key = Input.Keys.B)
    public void onPressT() {
        setVisible(true);
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "panel_build.lua");
//    }

}
