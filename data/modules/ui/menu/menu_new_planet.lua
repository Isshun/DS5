data:extend({
    type = "view",
    id = "base.ui.menu_new_planet",
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", size = {1920, 1200}},
        { type = "label", text = "Planets", text_size = 22, position = {500, 350}},
        { type = "list", id = "list_planets", position = {500, 400}},
        { type = "label", text = "next", text_size = 22, background = 0x121c1e, position = {1000, 800}, size = {100, 40}, on_click = function()
            application.ui:findById("base.ui.menu_new_planet"):setVisible(false)
            application.ui:findById("base.ui.menu_new_planet_region"):setVisible(true)
        end},
    },

    on_load = function(view)
        local list_planets = view:findById("list_planets")
        list_planets:removeAllViews();

        local iterator = data.planets:iterator()
        while iterator:hasNext() do
            local planet = iterator:next()
            local lb_planet = application.ui:createLabel()
            lb_planet:setText(planet.label)
            lb_planet:setTextSize(16)
            lb_planet:setSize(300, 32)
            lb_planet:setOnClickListener(function()
                select_planet(planet)
            end)
            list_planets:addView(lb_planet)
        end

        select_planet(data.planets:iterator():next())
    end,
})

function select_planet(planet)
    if planet.graphics.background then
        application.ui:findById("base.ui.menu_new_planet"):findById("img_background"):setVisible(true)
        application.ui:findById("base.ui.menu_new_planet"):findById("img_background"):setImage(planet.graphics.background.path)
    else
        application.ui:findById("base.ui.menu_new_planet"):findById("img_background"):setVisible(false)
    end

    application:sendEvent("new_game.planet", planet)
end