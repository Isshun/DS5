package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.render.layer.area.AreaLayer;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.engine.UIEventManager;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterGameLayerInit;
import org.smallbox.faraway.modules.area.AreaModel;
import org.smallbox.faraway.modules.area.AreaModule;
import org.smallbox.faraway.modules.area.AreaTypeInfo;

import java.util.Comparator;

@GameObject
public class AreaPanelController extends LuaController {

    @Inject private UIEventManager uiEventManager;
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private AreaModule areaModule;
    @Inject private MainPanelController mainPanelController;
    @Inject private AreaLayer areaLayer;
    @Inject private GameActionManager gameActionManager;
    @Inject private DependencyManager dependencyManager;

    @BindLua private UIList listAreasAdd;
    @BindLua private UIList listAreasSub;

    @AfterGameLayerInit
    public void afterGameLayerInit() {

        mainPanelController.addShortcut("Areas", this);

        dependencyManager.getGameDependencies().stream()
                .map(dependencyInfo -> dependencyInfo.dependency)
                .filter(dependency -> dependency.getClass().isAnnotationPresent(AreaTypeInfo.class))
                .sorted(Comparator.comparing(o -> o.getClass().getAnnotation(AreaTypeInfo.class).label()))
                .forEach(dependency -> {
                    UILabel lbArea = (UILabel) listAreasAdd.createFromTemplate();
                    lbArea.setText(" + " + dependency.getClass().getAnnotation(AreaTypeInfo.class).label());
                    lbArea.setOnClickListener((int x, int y) -> gameActionManager.setAreaAction(GameActionMode.ADD_AREA, (AreaModel) dependency));
                    listAreasAdd.addNextView(lbArea);
                });

        listAreasAdd.switchViews();
    }

    @Override
    public void onRefreshUI(int frame) {

    }

    @Override
    public void onMouseMove(int x, int y, int button) {
    }

}
