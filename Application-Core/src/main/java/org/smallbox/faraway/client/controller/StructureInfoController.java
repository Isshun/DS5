package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.View;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.game.world.Parcel;
import org.smallbox.faraway.game.structure.StructureItem;
import org.smallbox.faraway.game.structure.StructureModule;

import java.util.Queue;

@GameObject
public class StructureInfoController extends AbsInfoLuaController<StructureItem> {

    @Inject
    protected GameSelectionManager gameSelectionManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private StructureModule structureModule;

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
        progressHealth.getGeometry().setWidth(80 * structure.getHealth() / structure.getMaxHealth());

        if (!structure.isComplete()) {
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
                            .setTextColor(0x9afbffff)
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
                        .setTextColor(structure.getInventoryQuantity(componentInfo.component) < componentInfo.quantity ? 0xabababff : 0x9afbffff)
                        .setSize(100, 20)));
        listBuildComponents.switchViews();
    }

    @Override
    protected void onDisplayMultiple(Queue<StructureItem> objects) {
    }

    @Override
    public StructureItem getObjectOnParcel(Parcel parcel) {
        return structureModule.getStructure(parcel);
    }
}
