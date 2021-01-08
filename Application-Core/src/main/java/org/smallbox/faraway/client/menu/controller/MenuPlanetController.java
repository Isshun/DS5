package org.smallbox.faraway.client.menu.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import org.smallbox.faraway.client.ClientLuaModuleManager;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.lua.LuaControllerManager;
import org.smallbox.faraway.client.ui.UIManager;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.DependencyInjector;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

@ApplicationObject
public class MenuPlanetController extends LuaController {

    @Inject
    private GameManager gameManager;

    @Inject
    private GameFactory gameFactory;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private MenuCrewController menuCrewController;

    @Inject
    private Data data;

    @BindLua private UIList listPlanets;
    @BindLua private UIImage imgPlanet;
    @BindLua private View infoPlanet;
    @BindLua private UILabel lbInfoName;
    @BindLua private UIList listInfoRegions;

    private PlanetInfo planet;

    @AfterApplicationLayerInit
    private void afterApplicationLayerInit() {
        data.planets.forEach(planet -> {
            UILabel lbPlanet = new UILabel(null);
            lbPlanet.setId(planet.name);
            lbPlanet.setText(planet.label);
            lbPlanet.setTextSize(24);
            lbPlanet.setTextColor(Color.BLACK);
            lbPlanet.setPadding(20);
            lbPlanet.setSize(300, 42);
            lbPlanet.setOnClickListener((x, y) -> selectPlanet(planet));
            listPlanets.addView(lbPlanet);
        });
        selectPlanet(data.planets.get(0));
    }

    private void selectPlanet(PlanetInfo planet) {
        if (planet.graphics.background != null) {
            imgPlanet.setImage(planet.graphics.background.path);
            listPlanets.getViews().stream().map(view -> ((UILabel)view)).forEach(view -> view.setTextColor(view.getId().equals(planet.name) ? Colors.BLUE_LIGHT_5 : Colors.BLUE_LIGHT_3));
            this.planet = planet;

            infoPlanet.setVisible(true);
            lbInfoName.setText(planet.label);
            listInfoRegions.getViews().clear();

            planet.regions.forEach(region -> {
                UILabel lbRegion = new UILabel(null);
                lbRegion.setText(region.label);
                lbRegion.setTextSize(22);
                lbRegion.setTextColor(Colors.BLUE_LIGHT_3);
                listInfoRegions.addNextView(lbRegion);
            });

            listInfoRegions.switchViews();
        }
    }

    @BindLuaAction
    private void onActionNext(View view) {
        setVisible(false);
//        menuCrewController.setVisible(true);
        gameFactory.create(applicationConfig.debug.scenario);
    }

    @BindLuaAction
    private void onActionBack(View view) {
    }

    @GameShortcut(key = Input.Keys.UP)
    public void onPressUp() {
        selectPlanet(safePlanet(data.planets.indexOf(planet) - 1));
    }

    @GameShortcut(key = Input.Keys.DOWN)
    public void onPressDown() {
        selectPlanet(safePlanet(data.planets.indexOf(planet) + 1));
    }

    @GameShortcut(key = Input.Keys.F1)
    public void onRefreshUI() {
        DependencyInjector.getInstance().getDependency(UIManager.class).getMenuViews().remove(getRootView().getName());
        DependencyInjector.getInstance().getDependency(UIManager.class).getRootViews().removeIf(rootView -> rootView.getView() == getRootView());
        DependencyInjector.getInstance().getDependency(ClientLuaModuleManager.class).loadLuaFile("menu_new_planet.lua");
        DependencyInjector.getInstance().getDependency(LuaControllerManager.class).initController(this);
        afterApplicationLayerInit();
        setVisible(true);
        selectPlanet(planet);
    }

    private PlanetInfo safePlanet(int index) {
        return data.planets.get(Math.max(Math.min(index, data.planets.size()), 0));
    }

}
