package org.smallbox.faraway.client.controller;

import org.smallbox.faraway.GameEvent;
import org.smallbox.faraway.client.renderer.AreaRenderer;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.core.game.Game;
import org.smallbox.faraway.core.lua.BindLua;
import org.smallbox.faraway.core.module.area.AreaTypeInfo;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.util.Log;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaController extends LuaController {

    @BindComponent
    private UIEventManager uiEventManager;

    @BindModule
    private AreaModule areaModule;

    @BindLua
    private UIList listAreasAdd;

    @BindLua
    private UIList listAreasSub;

    @BindLuaController
    private MainPanelController mainPanelController;

    @BindComponent
    private AreaRenderer areaRenderer;

    @Override
    public void onReloadUI() {

        mainPanelController.addShortcut("Areas", this);

        areaModule.getAreaTypes().forEach(cls -> {

            listAreasAdd.addView(UILabel.create(null)
                    .setText(" + " + cls.getAnnotation(AreaTypeInfo.class).label())
                    .setTextColor(0xB4D4D3)
                    .setTextSize(18)
                    .setPadding(10)
                    .setSize(160, 40)
                    .setFocusBackgroundColor(0x25c9cb)
                    .setRegularBackgroundColor(0x121c1e)
                    .setOnClickListener(event -> {
                        areaRenderer.setMode(AreaRenderer.Mode.ADD, cls);
                        uiEventManager.setSelectionListener(parcels -> {
                            Log.warning(AreaController.class, "HELLO");
                            areaRenderer.setMode(AreaRenderer.Mode.NONE, cls);
                            areaModule.addArea(cls, parcels);
                        });
                    })
            );

            listAreasSub.addView(UILabel.create(null)
                    .setText(" - " + cls.getAnnotation(AreaTypeInfo.class).label())
                    .setTextColor(0xB4D4D3)
                    .setTextSize(18)
                    .setPadding(10)
                    .setSize(160, 40)
                    .setFocusBackgroundColor(0x25c9cb)
                    .setRegularBackgroundColor(0x121c1e)
                    .setOnClickListener(event -> {
                        areaRenderer.setMode(AreaRenderer.Mode.SUB, cls);
                        uiEventManager.setSelectionListener(parcels -> {
                            Log.warning(AreaController.class, "HELLO");
                            areaRenderer.setMode(AreaRenderer.Mode.NONE, cls);
                        });
                        //            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
                    })
            );

        });

    }

    @Override
    protected void onNewGameUpdate(Game game) {
    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(GameEvent event) {
    }
}
