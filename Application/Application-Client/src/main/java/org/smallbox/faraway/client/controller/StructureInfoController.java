package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.client.controller.annotation.BindLua;
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

    @BindLua
    private UILabel        lbName;

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
