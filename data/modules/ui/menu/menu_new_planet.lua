ui:extend({
    type = "view",
    id = "base.ui.menu.new_planet",
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", size = {1920, 1200}, effects = { type = "fade", duration = 250 }},
        { type = "view", position = {application.info.screen_width / 2 - 300 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "label", text = "Planets", text_size = 38},
            { type = "list", id = "list_planets", position = {0, 40}},
            { type = "label", id = "bt_back", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "back", position = {0, 350}, text_size = 22, size = {100, 40},
                on_click = function()
                    ui:find("base.ui.menu_new_planet"):setVisible(false)
                    ui:find("base.ui.menu_main"):setVisible(true)
                end},
            { type = "label", id = "bt_next", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "next", position = {200, 350}, text_size = 22, size = {100, 40}},
        }},
    },

    on_game_start = function(view)
        local list_planets = view:findById("list_planets")
        list_planets:removeAllViews();

        local iterator = data.planets:iterator()
        while iterator:hasNext() do
            local planet = iterator:next()
            local lb_planet = ui:createLabel()
            lb_planet:setText(planet.label)
            lb_planet:setTextSize(16)
            lb_planet:setSize(300, 34)
            lb_planet:setPadding(10)
            lb_planet:setOnClickListener(function()
                select_planet(lb_planet, planet)
            end)
            list_planets:addView(lb_planet)
        end

        select_planet(list_planets:getViews():get(0), data.planets:iterator():next())
    end,
})

function select_planet(lb_planet, planet)
    if planet.graphics.background then
        ui:find("base.ui.menu_new_planet"):findById("img_background"):setVisible(true)
        ui:find("base.ui.menu_new_planet"):findById("img_background"):setImage(planet.graphics.background.path)
    else
        ui:find("base.ui.menu_new_planet"):findById("img_background"):setVisible(false)
    end

    local iterator = lb_planet:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x55ffffff)
    end
    lb_planet:setBackgroundColor(0x8814dcb9)

    --    ui:find("base.ui.menu_new_planet"):findById("bt_next"):setBackgroundColor(0x8814dcb9)
    ui:find("base.ui.menu_new_planet"):findById("bt_next"):setOnClickListener(function()
        ui:find("base.ui.menu_new_planet"):setVisible(false)
        ui:find("base.ui.menu_new_planet_region"):setVisible(true)
    end)

    application:sendEvent("new_game.planet", planet)
end