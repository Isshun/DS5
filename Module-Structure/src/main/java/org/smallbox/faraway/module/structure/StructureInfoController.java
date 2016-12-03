package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.world.model.ParcelModel;
import org.smallbox.faraway.core.module.world.model.StructureModel;
import org.smallbox.faraway.module.mainPanel.controller.AbsInfoLuaController;

import java.util.List;

/**
 * Created by Alex on 26/04/2016.
 */
public class StructureInfoController extends AbsInfoLuaController<StructureModel> {
    @BindLua private UILabel        lbName;

    @BindModule
    private StructureModule structureModule;

    @Override
    protected void onDisplayUnique(StructureModel structure) {
        lbName.setText(structure.getLabel());
    }

    @Override
    protected void onDisplayMultiple(List<StructureModel> list) {

    }

    @Override
    protected StructureModel getObjectOnParcel(ParcelModel parcel) {
        return structureModule.getStructure(parcel);
    }
}
