package org.smallbox.faraway.client.controller.area;

import com.badlogic.gdx.graphics.Color;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIFrame;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.gameAction.OnGameSelectAction;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.storage.StorageArea;
import org.smallbox.faraway.modules.storage.StorageModule;

import java.util.Collection;
import java.util.Queue;
import java.util.stream.Collectors;

@GameObject
public class AreaInfoStorageController extends AbsInfoLuaController<AreaModel> {

    @Inject
    protected GameSelectionManager gameSelectionManager;

    @Inject
    private Data data;

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private AreaModule areaModule;

    @Inject
    private StorageModule storageModule;

    @Inject
    private AreaInfoController areaInfoController;

    @BindLua private UIList listStorage;
    @BindLua private UILabel btPriority1;
    @BindLua private UILabel btPriority2;
    @BindLua private UILabel btPriority3;
    @BindLua private UILabel btPriority4;
    @BindLua private UILabel btPriority5;
    @BindLua private UILabel lbSpace;

    private Collection<CategoryContainer> _tree;
    private StorageArea _area;

    private class CategoryContainer {
        public final String categoryName;
        public Collection<SubCategoryContainer> subCategories;
        public Collection<ItemInfo> items;
        public boolean isOpen;

        public CategoryContainer(String categoryName) {
            this.categoryName = categoryName;
            this.subCategories = data.consumables.stream()
                    .filter(consumable -> StringUtils.equals(categoryName, consumable.category))
                    .map(consumable -> consumable.subCategory)
                    .distinct()
                    .filter(StringUtils::isNotEmpty)
                    .map(subCategoryName -> new SubCategoryContainer(categoryName, subCategoryName))
                    .collect(Collectors.toList());
            this.items = data.consumables.stream()
                    .filter(itemInfo -> StringUtils.equals(categoryName, itemInfo.category))
                    .filter(itemInfo -> itemInfo.subCategory == null)
                    .collect(Collectors.toList());
        }
    }

    private class SubCategoryContainer {
        public final String subCategoryName;
        public Collection<ItemInfo> items;
        public boolean isOpen;

        public SubCategoryContainer(String categoryName, String subCategoryName) {
            this.subCategoryName = subCategoryName;
            this.items = data.consumables.stream()
                    .filter(itemInfo -> StringUtils.equals(categoryName, itemInfo.category))
                    .filter(itemInfo -> StringUtils.equals(subCategoryName, itemInfo.subCategory))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void onReloadUI() {
        gameSelectionManager.registerSelection(this, areaInfoController);

        // Creation d'un arbre contenant toutes les categories
        _tree = data.consumables.stream()
                .map(itemInfo -> itemInfo.category)
                .distinct()
                .map(CategoryContainer::new)
                .collect(Collectors.toList());

        btPriority1.setOnClickListener((int x, int y) -> setPriority(1));
        btPriority2.setOnClickListener((int x, int y) -> setPriority(2));
        btPriority3.setOnClickListener((int x, int y) -> setPriority(3));
        btPriority4.setOnClickListener((int x, int y) -> setPriority(4));
        btPriority5.setOnClickListener((int x, int y) -> setPriority(5));
    }

    private void setPriority(int priority) {
        _area.setPriority(priority);
        displayPriority(_area.getPriority());
    }

    private void displayPriority(int priority) {
        btPriority1.setBorderColor(priority == 1 ? Colors.COLOR1 : null);
        btPriority2.setBorderColor(priority == 2 ? Colors.COLOR1 : null);
        btPriority3.setBorderColor(priority == 3 ? Colors.COLOR1 : null);
        btPriority4.setBorderColor(priority == 4 ? Colors.COLOR1 : null);
        btPriority5.setBorderColor(priority == 5 ? Colors.COLOR1 : null);
    }

    private void displayTree() {

        listStorage.removeAllViews();

        // Display categories
        _tree.forEach(categoryContainer -> {
            UIFrame frameStorage = (UIFrame)listStorage.createFromTemplate();
            frameStorage.findLabel("lb_category").setText(categoryContainer.categoryName);
            frameStorage.findImage("img_category").setImage(getContainerImage(categoryContainer.categoryName));
            frameStorage.findById("img_category").setOnClickListener((int x, int y) -> {
                clickOnBox(categoryContainer.categoryName, null);
                storageModule.notifyRulesChange(_area);
                displayTree();
            });
            frameStorage.setOnClickListener((int x, int y) -> {
                categoryContainer.isOpen = !categoryContainer.isOpen;
                displayTree();
            });
            listStorage.addView(frameStorage);

//            // Display sub categories
//            if (categoryContainer.isOpen) {
//                categoryContainer.subCategories.forEach(subCategoryContainer -> {
//                    listStorage.addView(new UIFrame(null)
//                            .addView(UILabel.createFast((subCategoryContainer.isOpen ? " + " : " - ") + subCategoryContainer.subCategoryName, Colors.COLOR2).setPadding(5).setMargin(0, 0))
//                            .addView(UIImage.createFast(getContainerImage(categoryContainer.categoryName, subCategoryContainer.subCategoryName), 16, 16)
//                                    .setPosition(300, 0)
//                                    .setOnClickListener((int x, int y) -> {
//                                        clickOnBox(categoryContainer.categoryName, subCategoryContainer.subCategoryName);
//                                        storageModule.notifyRulesChange(_area);
//                                        displayTree();
//                                    }))
//                            .setSize(200, 20)
//                            .setOnClickListener((int x, int y) -> {
//                                subCategoryContainer.isOpen = !subCategoryContainer.isOpen;
//                                displayTree();
//                            })
//                    );
//
//                    // Display items
//                    if (subCategoryContainer.isOpen) {
//                        displayConsumables(subCategoryContainer.items);
//                    }
//                });
//
//                // Display items with no sub-cateogry
//                displayConsumables(categoryContainer.items);
//            }
        });

    }

    private void displayConsumables(Collection<ItemInfo> items) {
        items.forEach(itemInfo -> {
            listStorage.addView(new UIFrame(null)
                    .addView(UILabel.createFast("   " + itemInfo.label, Color.WHITE).setPadding(5, 5, 5, 5))
                    .addView(UIImage.createFast(getItemImage(itemInfo), 16, 16)
                            .setPosition(300, 0)
                            .setOnClickListener((int x, int y) -> {
                                _area.setAccept(itemInfo, !_area.isAccepted(itemInfo));
                                storageModule.notifyRulesChange(_area);
                                displayTree();
                            }))
                    .setSize(200, 20)
                    .setOnClickListener((int x, int y) -> {
                        _area.setAccept(itemInfo, !_area.isAccepted(itemInfo));
                        displayTree();
                    })
            );
        });
    }

    private void clickOnBox(String categoryName, String subCategoryName) {
        Collection<ItemInfo> items = data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(categoryName, itemInfo.category))
                .filter(itemInfo -> StringUtils.equals(subCategoryName, itemInfo.subCategory) || subCategoryName == null)
                .collect(Collectors.toList());

        boolean allItemsAreAccepted = items.stream().allMatch(itemInfo -> _area.isAccepted(itemInfo));
        items.forEach(itemInfo -> _area.setAccept(itemInfo, !allItemsAreAccepted));
    }

    private String getItemImage(ItemInfo itemInfo) {
        return _area.isAccepted(itemInfo)
                ? "[base]/graphics/icons/ic_ok.png"
                : "[base]/graphics/icons/ic_no.png";
    }

    private String getContainerImage(String categoryName) {
        return data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(categoryName, itemInfo.category))
                .allMatch(itemInfo -> _area.isAccepted(itemInfo))
                ? "[base]/graphics/icons/ic_ok.png"
                : "[base]/graphics/icons/ic_no.png";
    }

    private String getContainerImage(String categoryName, String subCategoryName) {
        return data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(categoryName, itemInfo.category))
                .filter(itemInfo -> StringUtils.equals(subCategoryName, itemInfo.subCategory))
                .allMatch(itemInfo -> _area.isAccepted(itemInfo))
                ? "[base]/graphics/icons/ic_ok.png"
                : "[base]/graphics/icons/ic_no.png";
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
        if (_area != area) {
            _area = (StorageArea) area;
            displayTree();
            displayPriority(_area.getPriority());
        }
        displaySpace(_area.getParcels().stream()
                .filter(parcel -> !parcel.hasItems())
                .mapToInt(parcel -> 1)
                .sum());
    }

    @OnGameSelectAction(StorageArea.class)
    public void onDisplayArea(StorageArea area) {
        _area = area;
        setVisible(true);
    }

    private void displaySpace(int space) {
        lbSpace.setText(space + "/" + _area.getParcels().size());
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
    }

    @Override
    public AreaModel getObjectOnParcel(ParcelModel parcel) {
        return areaModule.getArea(StorageArea.class, parcel);
    }
}
