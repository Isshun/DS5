package org.smallbox.faraway.client.controller.area;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.GameActionManager;
import org.smallbox.faraway.client.GameActionMode;
import org.smallbox.faraway.client.selection.SelectionManager;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.render.layer.AreaLayer;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaTypeInfo;

import java.util.Comparator;

@GameObject
public class AreaPanelController extends LuaController {

    @Inject
    private UIEventManager uiEventManager;

    @Inject
    private SelectionManager selectionManager;

    @Inject
    private AreaModule areaModule;

    @BindLua
    private UIList listAreasAdd;

    @BindLua
    private UIList listAreasSub;

    @Inject
    private MainPanelController mainPanelController;

    @Inject
    private AreaLayer areaLayer;

    @Inject
    private GameActionManager gameActionManager;

    @AfterGameLayerInit
    public void afterGameLayerInit() {

        mainPanelController.addShortcut("Areas", this);

        DependencyInjector.getInstance().getGameDependencies().stream()
                .map(dependencyInfo -> dependencyInfo.dependency)
                .filter(dependency -> dependency.getClass().isAnnotationPresent(AreaTypeInfo.class))
                .sorted(Comparator.comparing(o -> o.getClass().getAnnotation(AreaTypeInfo.class).label()))
                .forEach(dependency -> {

                    listAreasAdd.addView(UILabel.create(null)
                            .setText(" + " + dependency.getClass().getAnnotation(AreaTypeInfo.class).label())
                            .setTextColor(0xB4D4D3ff)
                            .setTextSize(18)
                            .setPadding(10)
                            .setSize(160, 40)
                            .setFocusBackgroundColor(0x25c9cbff)
                            .setRegularBackgroundColor(0x121c1eff)
                            .setOnClickListener((int x, int y) -> {
                                gameActionManager.setAreaAction(GameActionMode.ADD_AREA, (AreaModel)dependency);
//                                areaLayer.setMode(AreaLayer.Mode.ADD, cls);
//                                selectionManager.setSelectionListener(parcels -> {
//                                    areaLayer.setMode(AreaLayer.Mode.NONE, cls);
//                                    areaModule.addArea(cls, parcels);
//                                    return true;
//                                });
                            })
                    );

//                    listAreasSub.addView(UILabel.create(null)
//                            .setText(" - " + cls.getAnnotation(AreaTypeInfo.class).label())
//                            .setTextColor(0xB4D4D3ff)
//                            .setTextSize(18)
//                            .setPadding(10)
//                            .setSize(160, 40)
//                            .setFocusBackgroundColor(0x25c9cbff)
//                            .setRegularBackgroundColor(0x121c1eff)
//                            .setOnClickListener((int x, int y) -> {
//                                areaLayer.setMode(AreaLayer.Mode.SUB, cls);
//                                selectionManager.setSelectionListener(parcels -> {
//                                    areaLayer.setMode(AreaLayer.Mode.NONE, cls);
//                                    areaModule.removeArea(parcels);
//                                    gameActionManager.setMode(GameActionManager.Mode.REMOVE_AREA);
//                                    return true;
//                                });
//                                //            Application.gameManager.getGame().getInteraction().set(GameActionExtra.Action.REMOVE_AREA, AreaType.GARDEN);
//                            })
//                    );

                });

    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(int x, int y, int button) {
    }

    @GameShortcut(key = Input.Keys.A)
    public void onPressT() {
        setVisible(true);
    }
}
