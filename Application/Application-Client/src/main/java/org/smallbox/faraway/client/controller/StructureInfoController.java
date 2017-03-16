package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.modules.structure.StructureModule;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class StructureInfoController extends AbsInfoLuaController<StructureItem> {

    @BindModule
    private StructureModule structureModule;

    @BindLua private UILabel lbName;
    @BindLua private UILabel lbHealth;
    @BindLua private View progressHealth;
    @BindLua private UIList listInventory;

    @BindLua private View frameBuild;
    @BindLua private UILabel progressBuild;
    @BindLua private UIList listBuildComponents;

    @Override
    protected void onDisplayUnique(StructureItem structure) {
        lbName.setText(structure.getLabel());
        lbHealth.setText(String.valueOf(structure.getHealth()));
        progressHealth.setWidth(80 * structure.getHealth() / structure.getMaxHealth());

        if (!structure.isBuildComplete()) {
            frameBuild.setVisible(true);
            displayBuildPane(structure);
            return;
        } else {
            frameBuild.setVisible(false);
        }

        if (structure.getInventory() != null) {
            structure.getInventory().forEach(consumable ->
                    listInventory.addNextView(UILabel.create(null)
                            .setText(consumable.getInfo().label)
                            .setTextColor(0x9afbff)
                            .setSize(100, 20))
            );
            listInventory.switchViews();
        }

    }

    private void displayBuildPane(StructureItem structure) {
        progressBuild.setText(String.format("%3d%%", (int)(structure.getBuildProgress() * 100)));

        structure.getInfo().build.components.forEach(componentInfo ->
                listBuildComponents.addNextView(UILabel.create(null)
                        .setDashedString(componentInfo.component.label, structure.getInventoryQuantity(componentInfo.component) + " / " + componentInfo.quantity, 42)
                        .setTextColor(structure.getInventoryQuantity(componentInfo.component) < componentInfo.quantity ? 0xababab : 0x9afbff)
                        .setSize(100, 20)));
        listBuildComponents.switchViews();
    }

    @Override
    protected void onDisplayMultiple(List<StructureItem> list) {
    }

    @Override
    protected StructureItem getObjectOnParcel(ParcelModel parcel) {
        return structureModule.getStructure(parcel);
    }
}
