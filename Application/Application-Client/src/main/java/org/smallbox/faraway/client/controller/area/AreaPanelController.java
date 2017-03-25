package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaController;
import org.smallbox.faraway.client.render.layer.AreaLayer;
import org.smallbox.faraway.client.ui.engine.GameEvent;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.BindComponent;
import org.smallbox.faraway.core.dependencyInjector.BindModule;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaTypeInfo;
import org.smallbox.faraway.util.Log;

import java.util.Comparator;

/**
 * Created by Alex on 26/04/2016.
 */
public class AreaPanelController extends LuaController {

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
    private AreaLayer areaLayer;

    @Override
    public void onReloadUI() {

        mainPanelController.addShortcut("Areas", this);

        areaModule.getAreaClasses().stream()
                .sorted(Comparator.comparing(o -> o.getAnnotation(AreaTypeInfo.class).label()))
                .forEach(cls -> {

                    listAreasAdd.addView(UILabel.create(null)
                            .setText(" + " + cls.getAnnotation(AreaTypeInfo.class).label())
                            .setTextColor(0xB4D4D3)
                            .setTextSize(18)
                            .setPadding(10)
                            .setSize(160, 40)
                            .setFocusBackgroundColor(0x25c9cb)
                            .setRegularBackgroundColor(0x121c1e)
                            .setOnClickListener(event -> {
                                areaLayer.setMode(AreaLayer.Mode.ADD, cls);
                                uiEventManager.setSelectionListener(parcels -> {
                                    Log.warning(AreaPanelController.class, "HELLO");
                                    areaLayer.setMode(AreaLayer.Mode.NONE, cls);
                                    areaModule.addArea(cls, parcels);
                                    return true;
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
                                areaLayer.setMode(AreaLayer.Mode.SUB, cls);
                                uiEventManager.setSelectionListener(parcels -> {
                                    Log.warning(AreaPanelController.class, "HELLO");
                                    areaLayer.setMode(AreaLayer.Mode.NONE, cls);
                                    areaModule.removeArea(parcels);
                                    return true;
                                });
                                //            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
                            })
                    );

                });

    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(GameEvent event) {
    }
}
