data:extend({
    type = "view",
    id = "panel_main",
    position = {1200, 38},
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
                { type = "image", src = "[base]/graphics/fake_map.png", size = {380, 240}},
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
                { type = "label", size = {180, 40}, background = 0x349394, text = "Build", text_size = 18, padding = 10, shortcut = "open_build_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_build"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Plan", text_size = 18, padding = 10, shortcut = "open_plan_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_plan"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Stats", text_size = 18, padding = 10, shortcut = "open_stats_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_stats"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Crew", text_size = 18, padding = 10, shortcut = "open_crew_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_crew"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Jobs", text_size = 18, padding = 10, shortcut = "open_jobs_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_jobs"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Areas", text_size = 18, padding = 10, shortcut = "open_areas_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_areas"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Displays", text_size = 18, padding = 10, shortcut = "open_display_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_displays"):setVisible(true)
                end},
                { type = "label", size = {180, 40}, background = 0x349394, text = "Debug", text_size = 18, padding = 10, shortcut = "open_debug_panel", on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(false)
                    game.ui:findById("panel_debug"):setVisible(true)
                end},
            }}
        },
    },

    on_refresh = function(view)
        local resource_module = game:getModule("ResourceModule")
        if resource_module then
            view:findById("lb_resource_food"):setText(resource_module:getFoodCount());
            view:findById("lb_resource_wood"):setText(resource_module:getConsumableCount("base.wood_log"));
            view:findById("lb_resource_water"):setText(resource_module:getDrinkCount());
            view:findById("view_resource"):setVisible(true)
        else
            view:findById("view_resource"):setVisible(false)
        end
    end,

    on_event = function(view, event , data)
        if event == game.events.on_deselect then
            view:setVisible(true)
        end

        if event == game.events.on_speed_change then
            view:findById("lb_speed"):setText(data == 0 and "||" or ("x" .. data));

        end

        if event == game.events.on_resource_selected
                or event == game.events.on_character_selected
                --or event == game.events.on_parcel_selected
                or event == game.events.on_area_selected
                or event == game.events.on_structure_selected
                or event == game.events.on_item_selected
                or event == game.events.on_consumable_selected then
            view:setVisible(false)
        end
    end,
})