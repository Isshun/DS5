package org.smallbox.faraway.client.controller.area;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.MainPanelController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.gameAction.GameActionManager;
import org.smallbox.faraway.client.gameAction.GameActionMode;
import org.smallbox.faraway.client.layer.area.AreaLayer;
import org.smallbox.faraway.client.selection.GameSelectionManager;
import org.smallbox.faraway.client.ui.event.UIEventManager;
import org.smallbox.faraway.client.ui.widgets.CompositeView;
import org.smallbox.faraway.client.ui.widgets.UILabel;
import org.smallbox.faraway.client.ui.widgets.UIList;
import org.smallbox.faraway.core.dependencyInjector.DependencyManager;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.gameEvent.OnGameLayerComplete;
import org.smallbox.faraway.game.area.AreaModel;
import org.smallbox.faraway.game.area.AreaModule;
import org.smallbox.faraway.game.area.AreaTypeInfo;

import java.util.Comparator;

@GameObject
public class AreaPanelController extends LuaController {
    @Inject private GameSelectionManager gameSelectionManager;
    @Inject private MainPanelController mainPanelController;
    @Inject private GameActionManager gameActionManager;
    @Inject private DependencyManager dependencyManager;
    @Inject private UIEventManager uiEventManager;
    @Inject private AreaModule areaModule;
    @Inject private AreaLayer areaLayer;

    @BindLua private UIList listAreasAdd;
    @BindLua private UIList listAreasSub;

    @OnGameLayerComplete
    public void afterGameLayerInit() {
        dependencyManager.getGameDependencies().stream()
                .map(dependencyInfo -> dependencyInfo.dependency)
                .filter(dependency -> dependency.getClass().isAnnotationPresent(AreaTypeInfo.class))
                .sorted(Comparator.comparing(o -> o.getClass().getAnnotation(AreaTypeInfo.class).label()))
                .forEach(dependency -> {
                    CompositeView frameArea = listAreasAdd.createFromTemplate(CompositeView.class);
                    UILabel lbArea = frameArea.findLabel("lb_area");
                    lbArea.setText(dependency.getClass().getAnnotation(AreaTypeInfo.class).label());
                    frameArea.find("bt_add").getEvents().setOnClickListener(() -> gameActionManager.setAreaAction(GameActionMode.ADD_AREA, (AreaModel) dependency));
                    frameArea.find("bt_remove").getEvents().setOnClickListener(() -> gameActionManager.setAreaAction(GameActionMode.REMOVE_AREA, (AreaModel) dependency));
                    listAreasAdd.addNextView(frameArea);
                });

        listAreasAdd.switchViews();
    }

}
