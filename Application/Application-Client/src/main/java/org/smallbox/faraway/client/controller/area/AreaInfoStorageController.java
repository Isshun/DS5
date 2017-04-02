package org.smallbox.faraway.client.controller.area;

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

    @BindLua
    private UIGrid gridCategory;

    private String _subCategory;
    private String _category;
    private Map<String, Collection<ItemInfo>> _itemBySubCategory;
    private StorageArea _area;

    @Override
    public void onReloadUI() {
        uiEventManager.registerSelection(this, areaInfoController);

        // Creation d'une map contenant toutes les sous-categorie
        _itemBySubCategory = data.consumables.stream()
                .map(itemInfo -> itemInfo.category + "/" + itemInfo.subCategory)
                .distinct()
                .collect(Collectors.toMap(subCategory -> subCategory, itemInfo -> new ArrayList<>()));

        // Pour chaque sous-categorie ajout des consomables associés
        data.consumables.forEach(itemInfo -> _itemBySubCategory.get(itemInfo.category + "/" + itemInfo.subCategory).add(itemInfo));

        displayCategories();
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
        _area = (StorageArea) area;
    }

    private void displayCategories() {
        gridCategory.removeAllViews();
        data.consumables.stream()
                .map(itemInfo -> itemInfo.category)
                .distinct()
                .forEach(category -> gridCategory.addView(
                        UILabel.create(null)
                                .setText(category)
                                .setTextSize(14)
                                .setTextColor(ColorUtils.COLOR2)
                                .setTextAlign(View.Align.CENTER)
                                .setSize(80, 20)
                                .setOnClickListener(event -> displayCategory(category))
                ));
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
