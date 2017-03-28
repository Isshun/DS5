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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
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

    @BindLuaController
    private AreaInfoController areaInfoController;

    @BindLua
    private UIList listStorage;

    @BindLua
    private UIGrid gridCategory;

    private String _subCategory;
    private String _category;

    private Map<ItemInfo, Boolean> _items = new ConcurrentHashMap<>();
    private Map<String, Collection<ItemInfo>> _bySubCategory;

    @Override
    public void onReloadUI() {
        uiEventManager.registerSelection(this, areaInfoController);
        displayCategories();
    }

    @Override
    protected void onDisplayUnique(AreaModel area) {
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
                                .setSize(80, 20)
                                .setBackgroundColor(Color.CORAL)
                                .setOnClickListener(event -> {
                                    _category = category;
                                    displayByCategory();
                                })
                ));
    }

    private void displayByCategory() {
        _bySubCategory = data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(itemInfo.category, _category))
                .map(itemInfo -> itemInfo.subCategory)
                .distinct()
                .collect(Collectors.toMap(subCategory -> subCategory, itemInfo -> new ArrayList<>()));

        data.consumables.stream()
                .filter(itemInfo -> StringUtils.equals(itemInfo.category, _category))
                .forEach(itemInfo -> _bySubCategory.get(itemInfo.subCategory).add(itemInfo));

        displayBySubCategory();
    }

    private void displayBySubCategory() {
        listStorage.removeAllViews();
        for (Map.Entry<String, Collection<ItemInfo>> entry: _bySubCategory.entrySet()) {

            // Display sub category header
            listStorage.addView(new UIFrame(null)
                    .setSize(300, 27)
                    .addView(UIImage.create(null)
                            .setImage("[base]/graphics/bg_button_left.png")
                            .setPositionY(5)
                            .setSize(300, 22))
                    .addView(UICheckBox.create(null)
                            .setText(entry.getKey())
                            .setTextSize(14)
                            .setTextColor(ColorUtils.COLOR2)
                            .setPadding(5, 0)
                            .setMargin(2, 0)
                            .setSize(300, 22))
                    .setOnClickListener(event -> {
                        _subCategory = !StringUtils.equals(_subCategory, entry.getKey()) ? entry.getKey() : null;
                        displayBySubCategory();
                    }));

            // Display sub category consumables
            if (StringUtils.equals(_subCategory, entry.getKey())) {

                data.consumables.stream()
                        .filter(itemInfo -> StringUtils.equals(itemInfo.category, _category))
                        .filter(itemInfo -> StringUtils.equals(itemInfo.subCategory, _subCategory))
                        .forEach(itemInfo ->
                                listStorage.addView(UICheckBox.create(null)
                                        .setOnCheckListener(checked -> selectItem(itemInfo, checked == UICheckBox.Value.TRUE))
                                        .setChecked(_items.getOrDefault(itemInfo, false) ? UICheckBox.Value.TRUE : UICheckBox.Value.FALSE)
                                        .setText(itemInfo.label)
                                        .setTextSize(12)
                                        .setTextColor(ColorUtils.COLOR2)
//                                        .setBackgroundColor(_items.getOrDefault(itemInfo, false) ? Color.BLUE : Color.GREEN)
                                        .setPadding(5, 0)
                                        .setSize(100, 22)));
            }
        }
    }

    private void selectSubCategory(String subCategory, boolean accepted) {
        _bySubCategory.get(subCategory).forEach(itemInfo -> _items.put(itemInfo, accepted));
        displayBySubCategory();
    }

    private void selectItem(ItemInfo itemInfo, boolean accepted) {
        _items.put(itemInfo, accepted);
        displayBySubCategory();
    }

    @Override
    protected void onDisplayMultiple(Queue<AreaModel> objects) {
    }

    @Override
    public AreaModel getObjectOnParcel(ParcelModel parcel) {
        AreaModel area = areaModule.getArea(parcel);
        return area instanceof StorageArea ? area : null;
    }
}
