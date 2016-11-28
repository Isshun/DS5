package org.smallbox.faraway.module.structure;

import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.module.character.controller.LuaController;
import org.smallbox.faraway.core.module.world.model.StructureModel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 26/04/2016.
 */
public class InfoController extends LuaController {
    @BindLua private UILabel        lbName;

    @BindModule
    private StructureModule         _module;

    public StructureModel           _structure;

    @Override
    public void onGameStart(Game game) {
        _module.addObserver(new StructureModuleObserver() {
            @Override
            public void onSelectStructure(StructureModel structure) {
                _structure = structure;
                setVisible(true);
                refreshStructure(structure);
            }

            @Override
            public void onDeselectStructure(StructureModel lastStructure) {
                _structure = null;
                setVisible(false);
            }
        });
    }

    @Override
    public void onGameUpdate(Game game) {
        if (_structure != null) {
            refreshStructure(_structure);
        }
    }

    private void refreshStructure(StructureModel structure) {
        _structure = structure;

        lbName.setText(structure.getLabel());
    }
}
