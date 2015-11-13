data:extend({
    type = "view",
    id = "panel_main",
    position = {application.info.screen_width - 400, 38},
    size = {400, 800},
    background = 0x121c1e,
    visible = true,
    views = {
        type = "list",
        position = {10, 0},
        views = {
            { type = "view", position = {0, 12}, size = {380, 44}, background = 0x203636, views = {
                { type = "label", text = "Far Point", text_size = 16, padding = 10 },
                { type = "label", text = "Arrakis / Desert", text_size = 16, position = {220, 0}, padding = 10 },
            }},
            { type = "view", size = {380, 240}, views = {
--                { type = "image", id = "img_map", background = 0x000000, size = {380, 240}},
                { type = "label", id = "lb_floor", text = "0", text_size = 22, position = {12, 215}},
                { type = "label", id = "lb_speed", text = "x1", text_size = 22, position = {348, 215}},
            }},
            { type = "view", id = "view_resource", background = 0x203636, size = {380, 34}, views = {
                { type = "grid", id = "grid_resource", columns = 8, column_width = 70, views = {
                    { type = "view", size = {70, 32}, position = {0, 0}, views = {
                        { type = "image", src = "[base]/graphics/icons/food.png", size = {32, 32}},
                        { type = "label", id = "lb_resource_food", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                    }},
                    { type = "view", size = {70, 32}, position = {0, 0}, views = {
                        { type = "image", src = "[base]/graphics/icons/water.png", size = {32, 32}},
                        { type = "label", id = "lb_resource_water", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                    }},
                    { type = "view", size = {70, 32}, views = {
                        { type = "image", src = "[base]/graphics/icons/wood.png", size = {32, 32}},
                        { type = "label", id = "lb_resource_wood", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                    }},
                }},
                { type = "image", src = "[base]/graphics/icons/setting.png", position = {356, 8}, size = {32, 32}, on_click = function()
                end},
            }},
            { type = "grid", id = "main_grid", position = {0, 18}, columns = 2, column_width = 195, row_height = 60, views = {
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Build", text_size = 18, padding = 10, on_click = function() open_panel("panel_build") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Plan", text_size = 18, padding = 10, on_click = function() open_panel("panel_plan") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Displays", text_size = 18, padding = 10, on_click = function() open_panel("panel_displays") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Areas", text_size = 18, padding = 10, on_click = function() open_panel("panel_areas") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Jobs", text_size = 18, padding = 10, on_click = function() open_panel("panel_jobs") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Crew", text_size = 18, padding = 10, on_click = function() open_panel("panel_crew") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Stats", text_size = 18, padding = 10, on_click = function() open_panel("panel_stats") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Networks", text_size = 18, padding = 10, on_click = function() open_panel("panel_networks") end},
                { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Debug", text_size = 18, padding = 10, on_click = function() open_panel("panel_debug") end},
            }}
        },
    },

    on_load = function(view)
        view:findById("view_resource"):setVisible(application:getModule("ResourceModule"))
    end,

    on_refresh = function(view)
        local network_module = application:getModule("NetworkModule")
        if network_module then
            local water = 0
            local iterator = network_module:getNetworks():iterator()
            while iterator:hasNext() do
                local network = iterator:next()
                if network:getInfo().name == "base.network.water" then water = water + network:getQuantity() end
            end
            view:findById("lb_resource_water"):setText(math.floor(water));
        end

        local resource_module = application:getModule("ResourceModule")
        if resource_module then
            view:findById("lb_resource_food"):setText(resource_module:getFoodCount());
            view:findById("lb_resource_wood"):setText(resource_module:getConsumableCount("base.wood_log"));
        end
    end,

    on_event = function(view, event , data)

        -- Check bindings
        if event == application.events.on_binding then
            if data == application.bindings.open_panel_build then open_panel("panel_build") end
            if data == application.bindings.open_panel_plan then open_panel("panel_plan") end
            if data == application.bindings.open_panel_tasks then open_panel("panel_jobs") end
            if data == application.bindings.open_panel_crew then open_panel("panel_crew") end
            if data == application.bindings.open_panel_displays then open_panel("panel_displays") end
            if data == application.bindings.open_panel_stats then open_panel("panel_stats") end
            if data == application.bindings.open_panel_areas then open_panel("panel_areas") end
            if data == application.bindings.open_panel_debug then open_panel("panel_debug") end
        end

        if event == application.events.on_deselect then
            view:setVisible(true)
        end

        if event == application.events.on_floor_change then
            view:findById("lb_floor"):setText(data);
        end

        if event == application.events.on_speed_change then
            view:findById("lb_speed"):setText(data == 0 and "||" or ("x" .. data));
        end

        if event == application.events.on_resource_selected
                or event == application.events.on_character_selected
                --or event == application.events.on_parcel_selected
                or event == application.events.on_area_selected
                or event == application.events.on_structure_selected
                or event == application.events.on_item_selected
                or event == application.events.on_consumable_selected then
            view:setVisible(false)
        end
    end,
})

function open_panel(panel_name)
    application:sendEvent("mini_map.display", false)
    application.ui:findById("panel_main"):setVisible(false)
    application.ui:findById(panel_name):setVisible(true)
end