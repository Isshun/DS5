package org.smallbox.faraway.client.menu.controller;

import org.smallbox.faraway.client.controller.LuaController;
import org.smallbox.faraway.client.controller.annotation.BindLuaAction;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.dependencyInjector.annotation.ApplicationObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;
import org.smallbox.faraway.core.dependencyInjector.annotationEvent.AfterApplicationLayerInit;
import org.smallbox.faraway.core.game.GameFactory;
import org.smallbox.faraway.core.game.GameManager;
import org.smallbox.faraway.core.game.service.applicationConfig.ApplicationConfig;

@ApplicationObject
public class MenuCrewController extends LuaController {

    @Inject
    private GameManager gameManager;

    @Inject
    private GameFactory gameFactory;

    @Inject
    private ApplicationConfig applicationConfig;

    @Inject
    private MenuSettingsController menuSettingsController;

    @Inject
    private MenuCrewController menuCrewController;

    @AfterApplicationLayerInit
    private void afterApplicationLayerInit() {
    }

    @BindLuaAction
    private void onActionNext(View view) {
        setVisible(false);
        menuCrewController.setVisible(true);
    }

//
//    on_game_start = function(view)
//    local list_planets = view:findById("list_planets")
//    list_planets:removeAllViews();
//
//    local iterator = data.planets:iterator()
//        while iterator:hasNext() do
//    local planet = iterator:next()
//    local lb_planet = ui:createLabel()
//    lb_planet:setText(planet.label)
//    lb_planet:setTextSize(16)
//    lb_planet:setSize(300, 34)
//    lb_planet:setPadding(10)
//    lb_planet:setOnClickListener(function()
//    select_planet(lb_planet, planet)
//    end)
//    list_planets:addView(lb_planet)
//    end
//
//    select_planet(list_planets:getViews():get(0), data.planets:iterator():next())
//    end,



//
//    function select_planet(lb_planet, planet)
//    if planet.graphics.background then
//    ui:find("base.ui.menu_new_planet"):findById("img_background"):setVisible(true)
//    ui:find("base.ui.menu_new_planet"):findById("img_background"):setImage(planet.graphics.background.path)
//    else
//    ui:find("base.ui.menu_new_planet"):findById("img_background"):setVisible(false)
//    end
//
//    local iterator = lb_planet:getParent():getViews():iterator()
//    while iterator:hasNext() do
//    iterator:next():setBackgroundColor(0x55ffffff)
//    end
//    lb_planet:setBackgroundColor(0x8814dcb9)
//
//    --    ui:find("base.ui.menu_new_planet"):findById("bt_next"):setBackgroundColor(0x8814dcb9)
//    ui:find("base.ui.menu_new_planet"):findById("bt_next"):setOnClickListener(function()
//    ui:find("base.ui.menu_new_planet"):setVisible(false)
//    ui:find("base.ui.menu_new_planet_region"):setVisible(true)
//    end)
//
//    application:sendEvent("new_game.planet", planet)
//    end
}
