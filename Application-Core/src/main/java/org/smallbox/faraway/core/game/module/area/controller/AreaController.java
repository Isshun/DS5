package org.smallbox.faraway.core.game.module.area.controller;

import org.smallbox.faraway.core.BindModule;
import org.smallbox.faraway.core.game.BindLua;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.game.module.area.model.AreaType;
import org.smallbox.faraway.core.game.module.character.controller.LuaController;
import org.smallbox.faraway.core.game.module.ui.UIPanelModule;
import org.smallbox.faraway.ui.GameActionExtra;
import org.smallbox.faraway.ui.engine.views.widgets.UILabel;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaController extends LuaController {
    @BindModule("base.module.uiPanel")
    private UIPanelModule       uiPanelModule;

    @BindLua private UILabel    btAddStorage;
    @BindLua private UILabel    btRemoveStorage;

    @BindLua private UILabel    btAddDump;
    @BindLua private UILabel    btRemoveDump;

    @BindLua private UILabel    btAddHome;
    @BindLua private UILabel    btRemoveHome;

    @BindLua private UILabel    btAddSector;
    @BindLua private UILabel    btRemoveSector;

    @BindLua private UILabel    btAddGarden;
    @BindLua private UILabel    btRemoveGarden;

    @Override
    protected void onCreate() {
        btAddStorage.setOnClickListener(() -> {
            Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.STORAGE);
        });

        btRemoveStorage.setOnClickListener(() -> {
            Game.getInstance().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.STORAGE);
        });

//        btAddDump.setOnClickListener(() -> {
//            Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.STORAGE);
//        });
//
//        btRemoveDump.setOnClickListener(() -> {
//            Game.getInstance().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.STORAGE);
//        });

        btAddHome.setOnClickListener(() -> {
            Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.HOME);
        });

        btRemoveHome.setOnClickListener(() -> {
            Game.getInstance().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.HOME);
        });

//        btAddSector.setOnClickListener(() -> {
//            Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.STORAGE);
//        });
//
//        btRemoveSector.setOnClickListener(() -> {
//            Game.getInstance().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.STORAGE);
//        });

        btAddGarden.setOnClickListener(() -> {
            Game.getInstance().getInteraction().set(GameActionExtra.Action.SET_AREA, AreaType.GARDEN);
        });

        btRemoveGarden.setOnClickListener(() -> {
            Game.getInstance().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
        });
    }
}
