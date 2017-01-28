package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureItem;
import org.smallbox.faraway.module.mainPanel.controller.AbsInfoLuaController;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class StructureInfoController extends AbsInfoLuaController<StructureItem> {
    @BindLua private UILabel        lbName;

    @BindModule
    private StructureModule structureModule;

    @Override
    protected void onDisplayUnique(StructureItem structure) {
        lbName.setText(structure.getLabel());
    }

    @Override
    protected void onDisplayMultiple(List<StructureItem> list) {

    }

    @Override
    protected StructureItem getObjectOnParcel(ParcelModel parcel) {
        return structureModule.getStructure(parcel);
    }
}
