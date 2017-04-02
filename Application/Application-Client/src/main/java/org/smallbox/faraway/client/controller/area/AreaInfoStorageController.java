package org.smallbox.faraway.client.controller.area;

import com.badlogic.gdx.graphics.Color;
import org.apache.commons.lang3.StringUtils;
import org.smallbox.faraway.client.controller.AbsInfoLuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.*;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.engine.ColorUtils;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.consumable.StorageArea;
import org.smallbox.faraway.modules.storing.StoringModule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaInfoStorageController extends AbsInfoLuaController<AreaModel> {

    @BindComponent
    private Data data;

    @BindComponent
    private UIEventManager uiEventManager;

    @BindModule
    private AreaModule areaModule;

    @BindModule
    private StoringModule storingModule;

    @BindLuaController
    private AreaInfoController areaInfoController;

    @BindLua
    private UIList listStorage;

//    @BindLua
//    private UIGrid gridCategory;

    private String _subCategory;
    private String _category;
    //    private Map<String, Collection<ItemInfo>> _itemBySubCategory;
    private Collection<CategoryContainer> _tree;
    private StorageArea _area;

    private class CategoryContainer {
        public final String categoryName;
        public Collection<SubCategoryContainer> subCategories;
        public boolean isOpen;

        public CategoryContainer(String categoryName) {
            this.categoryName = categoryName;
            this.subCategories = data.consumables.stream()
                    .filter(consumable -> StringUtils.equals(categoryName, consumable.category))
                    .map(consumable -> consumable.subCategory)
                    .distinct()
                    .map(subCategoryName -> new SubCategoryContainer(categoryName, subCategoryName))
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
        uiEventManager.registerSelection(this, areaInfoController);

        // Creation d'un arbre contenant toutes les categories
        _tree = data.consumables.stream()
                .map(itemInfo -> itemInfo.category)
                .distinct()
                .map(CategoryContainer::new)
                .collect(Collectors.toList());

//        // Creation d'une map contenant toutes les sous-categorie
//        _itemBySubCategory = data.consumables.stream()
//                .map(itemInfo -> itemInfo.category + "/" + itemInfo.subCategory)
//                .distinct()
//                .collect(Collectors.toMap(subCategory -> subCategory, itemInfo -> new ArrayList<>()));
//
//        // Pour chaque sous-categorie ajout des consomables associés
//        data.consumables.forEach(itemInfo -> _itemBySubCategory.get(itemInfo.category + "/" + itemInfo.subCategory).add(itemInfo));

    }

    private void displayTree() {

        listStorage.removeAllViews();

        // Display categories
        _tree.forEach(categoryContainer -> {
            listStorage.addView(new UIFrame(null)
                    .setSize(200, 20)
                    .setOnClickListener(event -> {
                        categoryContainer.isOpen = !categoryContainer.isOpen;
                        displayTree();
                    })
                    .addView(UILabel.createFast(categoryContainer.categoryName, ColorUtils.COLOR1).setPadding(5))
                    .addView(UIImage.createFast(getContainerImage(categoryContainer.categoryName, null), 16, 16)
                            .setPosition(300, 0)
                            .setOnClickListener(event -> {
                                clickOnBox(categoryContainer.categoryName, null);
                                storingModule.notifyRulesChange(_area);
                                displayTree();
                            })
                    )
            );

            // Display sub categories
            if (categoryContainer.isOpen) {
                categoryContainer.subCategories.forEach(subCategoryContainer -> {
                    listStorage.addView(new UIFrame(null)
                            .setSize(200, 20)
                            .setOnClickListener(event -> {
                                subCategoryContainer.isOpen = !subCategoryContainer.isOpen;
                                displayTree();
                            })
                            .addView(UILabel.createFast(subCategoryContainer.subCategoryName, ColorUtils.COLOR2).setPadding(5).setMargin(0, 20))
                            .addView(UIImage.createFast(getContainerImage(categoryContainer.categoryName, subCategoryContainer.subCategoryName), 16, 16)
                                    .setPosition(300, 0)
                                    .setOnClickListener(event -> {
                                        clickOnBox(categoryContainer.categoryName, subCategoryContainer.subCategoryName);
                                        storingModule.notifyRulesChange(_area);
                                        displayTree();
                                    }))
                    );

                    // Display items
                    if (subCategoryContainer.isOpen) {
                        subCategoryContainer.items.forEach(itemInfo -> {

                            listStorage.addView(new UIFrame(null)
                                    .setSize(200, 20)
                                    .setOnClickListener(event -> {
                                        _area.setAccept(itemInfo, !_area.isAccepted(itemInfo));
                                        displayTree();
                                    })
                                    .addView(UILabel.createFast(itemInfo.label, Color.WHITE).setPadding(5).setMargin(0, 40))
                                    .addView(UIImage.createFast(getItemImage(itemInfo), 16, 16)
                                            .setPosition(300, 0)
                                            .setOnClickListener(event -> {
                                                _area.setAccept(itemInfo, !_area.isAccepted(itemInfo));
                                                storingModule.notifyRulesChange(_area);
                                                displayTree();
                                            }))
                            );

                        });
                    }
                });
            }
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

    private String getContainerImage(String categoryName, String subCategoryName) {
        return data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(categoryName, itemInfo.category))
                .filter(itemInfo -> StringUtils.equals(subCategoryName, itemInfo.subCategory) || subCategoryName == null)
                .allMatch(itemInfo -> _area.isAccepted(itemInfo))
                ? "[base]/graphics/icons/ic_ok.png"
                : "[base]/graphics/icons/ic_no.png";
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
        if (_area != area) {
            _area = (StorageArea) area;
            displayTree();
        }
    }

    private void displayCategories() {
//        gridCategory.removeAllViews();
//        data.consumables.stream()
//                .map(itemInfo -> itemInfo.category)
//                .distinct()
//                .forEach(category -> gridCategory.addView(
//                        UILabel.create(null)
//                                .setText(category)
//                                .setTextSize(14)
//                                .setTextColor(ColorUtils.COLOR2)
//                                .setTextAlign(View.Align.CENTER)
//                                .setSize(80, 20)
//                                .setOnClickListener(event -> displayCategory(category))
//                ));
    }

    private void refresh() {
        displayCategory(_category);
    }

    private void displayCategory(String category) {
        _category = category;

        List<String> subCategories = data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(itemInfo.category, category))
                .map(itemInfo -> itemInfo.subCategory)
                .distinct()
                .collect(Collectors.toList());

        listStorage.removeAllViews();

        subCategories.forEach(subCategory -> displaySubCategory(category, subCategory));
    }

    private void displaySubCategory(String category, String subCategory) {
        _category = category;

        boolean allItemsAreAccepted = data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(itemInfo.category, category))
                .filter(itemInfo -> StringUtils.equals(itemInfo.subCategory, subCategory))
                .allMatch(itemInfo -> _area.isAccepted(itemInfo));

        // Display sub category header
        listStorage.addView(new UIFrame(null)
                .setSize(300, 27)
                .addView(UIImage.create(null)
                        .setImage("[base]/graphics/bg_button_left.png")
                        .setPositionY(5)
                        .setSize(300, 22))
                .addView(UICheckBox.create(null)
                        .setText(subCategory)
                        .setTextSize(14)
                        .setTextColor(ColorUtils.COLOR2)
                        .setChecked(allItemsAreAccepted)
                        .setOnCheckListener((checked, clickOnBox) -> {
                            if (clickOnBox) {
                                data.consumables.stream()
                                        .filter(itemInfo -> StringUtils.equals(itemInfo.category, category))
                                        .filter(itemInfo -> StringUtils.equals(itemInfo.subCategory, subCategory))
                                        .forEach(itemInfo -> _area.setAccept(itemInfo, checked));
                                storingModule.notifyRulesChange(_area);
                                displayCategory(category);
                            } else {
                                toggleSubCategory(category, subCategory);
                            }
                        })
                        .setPadding(11, 8)
                        .setSize(300, 22))
        );

        // Display sub category consumables
        if (StringUtils.equals(_subCategory, subCategory)) {
            data.consumables.stream()
                    .filter(itemInfo -> StringUtils.equals(itemInfo.category, _category))
                    .filter(itemInfo -> StringUtils.equals(itemInfo.subCategory, _subCategory))
                    .forEach(this::displayConsumable);
        }

    }

    private void displayConsumable(ItemInfo itemInfo) {
        listStorage.addView(UICheckBox.create(null)
                .setOnCheckListener((checked, clickOnBox) -> {
                    _area.setAccept(itemInfo, checked);
                    storingModule.notifyRulesChange(_area);
                    refresh();
                })
                .setChecked(_area.isAccepted(itemInfo))
                .setText(itemInfo.label)
                .setTextSize(12)
                .setTextColor(ColorUtils.COLOR2)
                .setPadding(8, 5)
                .setSize(100, 22));
    }

    private void toggleSubCategory(String category, String subCategory) {
        _subCategory = !StringUtils.equals(_subCategory, subCategory) ? subCategory : null;
        displayCategory(category);
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
    }

    @Override
    public AreaModel getObjectOnParcel(ParcelModel parcel) {
        return areaModule.getArea(StorageArea.class, parcel);
    }
}
