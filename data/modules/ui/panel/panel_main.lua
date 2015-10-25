data:extend({
    type = "view",
    id = "panel_main",
    position = {1200, 65},
    size = {400, 800},
    background = 0x121c1e,
    visible = true,
    views = {
        type = "list",
        views = {
            { type = "view", position = {10, 10}, size = {-1, 32}, views = {
                { type = "label", text = "Far Point", text_size = 16, position = {0, 0}},
                { type = "label", text = "Arrakis / Desert", text_size = 16, position = {220, 0}},
            }},
            { type = "image", src = "data/graphics/fake_map.png", position = {10, 0}, size = {380, 240}},
            { type = "grid", id = "main_grid", position = {10, 16}, columns = 2, column_width = 195, row_height = 60, views = {
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
            }}
        },
    },

    on_event =
    function(event, view, data)
        if event == game.events.on_deselect then
            view:setVisible(true)
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