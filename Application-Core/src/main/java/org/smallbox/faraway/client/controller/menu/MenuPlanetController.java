package org.smallbox.faraway.client.controller.menu;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLua;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.shortcut.GameShortcut;
import org.smallbox.faraway.client.ui.widgets.*;
import org.smallbox.faraway.core.config.ApplicationConfig;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotation.callback.applicationEvent.OnApplicationLayerComplete;
import org.smallbox.faraway.core.game.DataManager;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.modelInfo.ItemInfo;
import org.smallbox.faraway.game.planet.PlanetInfo;

import static org.smallbox.faraway.client.ui.extra.Colors.BLUE_DARK_1;
import static org.smallbox.faraway.client.ui.extra.Colors.BLUE_LIGHT_5;

@ApplicationObject
public class MenuPlanetController extends LuaController {
    @Inject private GameManager gameManager;
    @Inject private GameFactory gameFactory;
    @Inject private ApplicationConfig applicationConfig;
    @Inject private MenuCrewController menuCrewController;
    @Inject private MenuMainController menuMainController;
    @Inject private DataManager dataManager;

    @BindLua private UIList listPlanets;
    @BindLua private UIImage imgPlanet;
    @BindLua private View infoPlanet;
    @BindLua private UILabel lbInfoName;
    @BindLua private UIList listInfoRegions;

    private PlanetInfo planet;

    @OnApplicationLayerComplete
    private void afterApplicationLayerInit() {
        dataManager.planets.forEach(planet -> {
            CompositeView viewPlanet = listPlanets.createFromTemplate(CompositeView.class);
            viewPlanet.setId(planet.name);

            UILabel lbPlanet = viewPlanet.findLabel("lb_planet");
            lbPlanet.setText(planet.label);
            lbPlanet.getEvents().setOnClickListener(() -> selectPlanet(planet));

            listPlanets.addView(viewPlanet);
        });
        selectPlanet(dataManager.planets.get(0));
    }

    private void selectPlanet(PlanetInfo planet) {
        if (planet.graphics.background != null) {
            imgPlanet.setImage(planet.graphics.background.path);
            listPlanets.getViews().forEach(view -> ((CompositeView)view).findLabel("lb_planet").setTextColor(view.getId().equals(planet.name) ? BLUE_LIGHT_5 : BLUE_DARK_1));
            this.planet = planet;

            infoPlanet.setVisible(true);
            lbInfoName.setText(planet.label);
            listInfoRegions.getViews().clear();

            planet.regions.forEach(region -> {
                CompositeView viewRegion = (CompositeView) listInfoRegions.createFromTemplate();
                ((UILabel) viewRegion.find("lb_region_name")).setText(region.label);
                ((UILabel) viewRegion.find("lb_hostility")).setText("low");
                ((UILabel) viewRegion.find("lb_fertility")).setText("high");
                ((UILabel) viewRegion.find("lb_atmosphere")).setText("o2, nitrogen");
                ((UILabel) viewRegion.find("lb_temperature")).setText("-5° +50");

                UIGrid listResource = ((UIGrid) viewRegion.find("grid_info_resources"));
                region.terrains.stream().filter(terrain -> terrain.resource != null).forEach(terrain -> {
                    CompositeView viewResource = listResource.createFromTemplate(CompositeView.class);
                    ItemInfo resourceInfo = dataManager.getItemInfo(terrain.resource);
                    if (resourceInfo.hasIcon()) {
                        viewResource.findImage("img_resource").setImage(resourceInfo.icon);
                    }
                    viewResource.findLabel("lb_resource").setText(resourceInfo.label);
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

    @GameShortcut("ui/up")
    public void onPressUp() {
        selectPlanet(safePlanet(dataManager.planets.indexOf(planet) - 1));
    }

    @GameShortcut("ui/down")
    public void onPressDown() {
        selectPlanet(safePlanet(dataManager.planets.indexOf(planet) + 1));
    }

//    @GameShortcut(key = Input.Keys.F1)
//    public void onRefreshUI() {
//        DependencyManager.getInstance().getDependency(UIManager.class).refresh(this, "menu_new_planet.lua");
//        selectPlanet(planet);
//    }

    private PlanetInfo safePlanet(int index) {
        return dataManager.planets.get(Math.max(Math.min(index, dataManager.planets.size()), 0));
    }

}
