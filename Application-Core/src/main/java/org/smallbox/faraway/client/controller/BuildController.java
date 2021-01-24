package org.smallbox.faraway.client.controller;

import com.badlogic.gdx.Input;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.asset.SpriteManager;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.OnClickListener;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.UIFrame;
import org.smallbox.faraway.client.ui.widgets.UIGrid;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.item.ItemModule;
import org.smallbox.faraway.game.structure.StructureModule;
import org.smallbox.faraway.game.world.WorldModule;

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
    @Inject private DataManager dataManager;

    @BindLua private CompositeView listCategories;
    @BindLua private UILabel contentLabel;
    @BindLua private UILabel lbCategory;
    @BindLua private UIGrid gridItems;

    private ItemInfo _currentItem;
    private Map<String, List<ItemInfo>> itemsByCategory;

    @AfterGameLayerInit
    public void afterGameLayerInit() {
        List<String> categories = List.of("structure", "furniture", "kitchen", "power", "industrial", "production", "other");

        // Build item map by category
        itemsByCategory = dataManager.getItems().stream()
                .filter(itemInfo -> StringUtils.equals(itemInfo.type, "item") || StringUtils.equals(itemInfo.type, "structure"))
                .collect(Collectors.groupingBy(itemInfo -> categories.contains(itemInfo.category) ? itemInfo.category : "other"));

        // Add item's categories to UI
        categories.forEach(category -> {
            UIFrame viewCategory = listCategories.createFromTemplate(UIFrame.class);
            viewCategory.getEvents().setOnClickListener(() -> openCategory(viewCategory, category));
            viewCategory.findImage("img_category").setImage("[base]/graphics/items/bt_categories/" + category + ".png");
            listCategories.addNextView(viewCategory);
        });
        listCategories.switchViews();

        // Open first category
        listCategories.getViews().stream().findFirst().ifPresent(view -> view.getEvents()._onClickListener.onClick());
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
        listCategories.getViews().forEach(view -> ((CompositeView) view).find("bg_category_off").setVisible(true));
        listCategories.getViews().forEach(view -> ((CompositeView) view).find("bg_category_on").setVisible(false));
//        viewCategory.findImage("img_category").getStyle().setBackgroundColor(Colors.BLUE_LIGHT_2);
        viewCategory.find("bg_category_off").setVisible(false);
        viewCategory.find("bg_category_on").setVisible(true);

        lbCategory.setText(currentCategory);

        Optional.ofNullable(itemsByCategory.get(currentCategory)).ifPresent(itemInfos -> itemInfos.forEach(entry -> {
            CompositeView viewItem = gridItems.createFromTemplate(CompositeView.class);
            viewItem.findLabel("lb_item").setText(entry.label);
            viewItem.findImage("img_item").setImage(ObjectUtils.firstNonNull(entry.icon, entry.defaultGraphic));
            viewItem.getEvents().setOnClickListener(() -> gameActionManager.setBuildAction(entry));
            gridItems.addNextView(viewItem);
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
