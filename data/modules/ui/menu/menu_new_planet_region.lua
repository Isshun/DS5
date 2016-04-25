data:extend({
    type = "view",
    id = "base.ui.menu_new_planet_region",
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", size = {1920, 1200}},
        { type = "view", position = {application.info.screen_width / 2 - 300 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "label", text = "Regions", text_size = 38},
            { type = "list", id = "list_regions", position = {0, 40}},
            { type = "label", id = "bt_back", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "back", position = {0, 350}, text_size = 22, size = {100, 40},
                on_click = function()
                    ui:find("base.ui.menu_new_planet_region"):setVisible(false)
                    ui:find("base.ui.menu_new_planet"):setVisible(true)
                end},
            { type = "label", id = "bt_next", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "next", text_size = 22, size = {100, 40}, position = {200, 350}},
        }}
    },

    on_event = function(view, event, data)
        if event == "new_game.planet" then
            if data.graphics.background then
                ui:find("base.ui.menu_new_planet_region"):findById("img_background"):setVisible(true)
                ui:find("base.ui.menu_new_planet_region"):findById("img_background"):setImage(data.graphics.background.path)
            else
                ui:find("base.ui.menu_new_planet_region"):findById("img_background"):setVisible(false)
            end

            local list_regions = view:findById("list_regions")
            list_regions:removeAllViews();

            local iterator = data.regions:iterator()
            while iterator:hasNext() do
                local region = iterator:next()
                local lb_region = ui:createLabel()
                lb_region:setText(region.label)
                lb_region:setTextSize(16)
                lb_region:setSize(300, 34)
                lb_region:setPadding(10)
                lb_region:setOnClickListener(function()
                    select_region(lb_region, data, region)
                end)
                list_regions:addView(lb_region)
            end

            select_region(list_regions:getViews():get(0), data.regions:iterator():next())
        end
    end,

})

function select_region(lb_region, region)
    application:sendEvent("new_game.region", region)

    local iterator = lb_region:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x55ffffff)
    end
    lb_region:setBackgroundColor(0x8814dcb9)

--    ui:find("base.ui.menu_new_planet_region"):findById("bt_next"):setBackgroundColor(0x8814dcb9)
    ui:find("base.ui.menu_new_planet_region"):findById("bt_next"):setOnClickListener(function()
        ui:find("base.ui.menu_new_planet_region"):setVisible(false)
        ui:find("base.ui.menu_new_crew"):setVisible(true)
    end)

end