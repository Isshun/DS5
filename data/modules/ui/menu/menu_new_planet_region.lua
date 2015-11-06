data:extend({
    type = "view",
    id = "base.ui.menu_new_planet_region",
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", size = {1920, 1200}},
        { type = "label", text = "Regions", text_size = 22, position = {500, 350}},
        { type = "list", id = "list_regions", position = {500, 400}},
        { type = "label", text = "next", text_size = 22, background = 0x121c1e, size = {100, 40}, position = {1000, 800}, on_click = function()
            application.ui:findById("base.ui.menu_new_planet_region"):setVisible(false)
            application.ui:findById("base.ui.menu_new_crew"):setVisible(true)
        end},
    },

    on_event = function(view, event, data)
        if event == "new_game.planet" then
            if data.graphics.background then
                application.ui:findById("base.ui.menu_new_planet_region"):findById("img_background"):setVisible(true)
                application.ui:findById("base.ui.menu_new_planet_region"):findById("img_background"):setImage(data.graphics.background.path)
            else
                application.ui:findById("base.ui.menu_new_planet_region"):findById("img_background"):setVisible(false)
            end

            local list_regions = view:findById("list_regions")
            list_regions:removeAllViews();

            local iterator = data.regions:iterator()
            while iterator:hasNext() do
                local region = iterator:next()
                local lb_region = application.ui:createLabel()
                lb_region:setText(region.label)
                lb_region:setTextSize(16)
                lb_region:setSize(300, 32)
                lb_region:setOnClickListener(function()
                    select_region(data, region)
                end)
                list_regions:addView(lb_region)
            end

            select_region(data.regions:iterator():next())
        end
    end,

})

function select_region(planet, region)
    application:sendEvent("new_game.region", region)
end