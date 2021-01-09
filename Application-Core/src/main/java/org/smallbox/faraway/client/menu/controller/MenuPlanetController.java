package org.smallbox.faraway.client.menu.controller;

import com.badlogic.gdx.Input;
import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.Colors;
import org.smallbox.faraway.client.ui.engine.views.CompositeView;
import org.smallbox.faraway.client.ui.engine.views.View;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIGrid;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIImage;
import org.smallbox.faraway.client.ui.engine.views.widgets.UILabel;
import org.smallbox.faraway.client.ui.engine.views.widgets.UIList;
import org.smallbox.faraway.core.GameShortcut;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.game.Data;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.model.planet.PlanetInfo;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
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
    private MenuMainController menuMainController;

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
            UILabel lbPlanet = listPlanets.createFromTemplate(UILabel.class);
            lbPlanet.setId(planet.name);
            lbPlanet.setText(planet.label);
            lbPlanet.getEvents().setOnClickListener((x, y) -> selectPlanet(planet));
            listPlanets.addView(lbPlanet);
        });
        selectPlanet(data.planets.get(0));
    }

    private void selectPlanet(PlanetInfo planet) {
        if (planet.graphics.background != null) {
            imgPlanet.setImage(planet.graphics.background.path);
            listPlanets.getViews().stream().map(view -> ((UILabel)view)).forEach(view -> view.setTextColor(view.getId().equals(planet.name) ? Colors.BLUE_LIGHT_5 : Colors.BLUE_DARK_1));
            this.planet = planet;

            infoPlanet.setVisible(true);
            lbInfoName.setText(planet.label);
            listInfoRegions.getViews().clear();

            planet.regions.forEach(region -> {
                CompositeView viewRegion = (CompositeView)listInfoRegions.createFromTemplate();
                ((UILabel)viewRegion.find("lb_region_name")).setText(region.label);
                ((UILabel)viewRegion.find("lb_hostility")).setText("low");
                ((UILabel)viewRegion.find("lb_fertility")).setText("high");
                ((UILabel)viewRegion.find("lb_atmosphere")).setText("o2, nitrogen");
                ((UILabel)viewRegion.find("lb_temperature")).setText("-5° +50");

                UIGrid listResource = ((UIGrid)viewRegion.find("grid_info_resources"));
                region.terrains.stream().filter(terrain -> terrain.resource != null).forEach(terrain -> {
                    UIImage viewResource = (UIImage)listResource.createFromTemplate();
                    ItemInfo resourceInfo = data.getItemInfo(terrain.resource);
                    if (resourceInfo.hasIcon()) {
                        viewResource.setImage(resourceInfo.icon);
                    }
                    listResource.addNextView(viewResource);
                });
                listResource.switchViews();

                listInfoRegions.addNextView(viewRegion);
            });

            listInfoRegions.switchViews();
        }
    }

    @BindLuaAction
    private void onActionBack(View view) {
        setVisible(false);
        menuMainController.setVisible(true);
    }

    @BindLuaAction
    private void onActionNext(View view) {
        setVisible(false);
        menuCrewController.setVisible(true);
    }

    @GameShortcut(key = Input.Keys.UP)
    public void onPressUp() {
        selectPlanet(safePlanet(data.planets.indexOf(planet) - 1));
    }

    @GameShortcut(key = Input.Keys.DOWN)
    public void onPressDown() {
        selectPlanet(safePlanet(data.planets.indexOf(planet) + 1));
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "menu_new_planet.lua");
//        selectPlanet(planet);
//    }

    private PlanetInfo safePlanet(int index) {
        return data.planets.get(Math.max(Math.min(index, data.planets.size()), 0));
    }

}