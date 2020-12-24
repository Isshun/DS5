ui:extend({
    type = "view",
    id = "base.ui.right_panel.content",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.MainPanelController",
    visible = true,
    debug = true,
    layer = -1,
    views = {{
        type = "list",
        position = {10, 0},
        id = "content_list",
        views = {
            { type = "view", id = "map_header", position = {0, 12}, size = {352, 44}, background = 0x203636ff, views = {
                { type = "label", id = "lb_planet", text_size = 16, text_color = color2, padding = 10 },
                { type = "label", id = "lb_floor", text_size = 16, text_color = color2, padding = 10, position = {250, 0} },
            }},
            { type = "view", id = "map_container", size = {352, 230}, background = 0x000000ff, views = {
                { type = "minimap"},
                { type = "image", id = "img_speed", position = {316, 205}, size = {32, 32}},
            }},
            { type = "view", id = "map_footer", size = {352, 38}, views = {
                { type = "grid", columns = 20, column_width = 30, row_height = 38, background = 0x203636ff, size = {352, 34}, views = {
                    { type = "label", text = "A", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_areas", on_click = function(v) application:toggleDisplay("areas") end},
                    { type = "label", text = "R", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_rooms", on_click = function(v) application:toggleDisplay("rooms") end},
                    { type = "label", text = "T", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_temperature", on_click = function(v) application:toggleDisplay("temperature") end},
                    { type = "label", text = "O", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_oxygen", on_click = function(v) application:toggleDisplay("oxygen") end},
                    { type = "label", text = "W", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_water", on_click = function(v) application:toggleDisplay("water") end},
                    { type = "label", text = "S", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_security", on_click = function(v) application:toggleDisplay("security") end},
                }},
            }},
            { type = "view", size = {300, 84}, views = {
                { type = "view", id = "bt_crew", size = {72, 84}, position = {0, 5}, views = {
                    { type = "image", id = "gauge_food", position = {0, 0}, size = {72, 84}, src = "[base]/graphics/icons/crewmate.png"},
                }},
                { type = "view", id = "bt_build", size = {72, 84}, position = {84, 5}, views = {
                    { type = "image", id = "gauge_food", position = {0, 0}, size = {72, 84}, src = "[base]/graphics/icons/build.png"},
                }},
                { type = "view", id = "bt_area", size = {72, 84}, position = {168, 5}, views = {
                    { type = "image", id = "gauge_food", position = {0, 0}, size = {72, 84}, src = "[base]/graphics/icons/area.png"},
                }},
                { type = "view", id = "bt_jobs", size = {72, 84}, position = {252, 5}, views = {
                    { type = "image", id = "gauge_food", position = {0, 0}, size = {72, 84}, src = "[base]/graphics/icons/jobs.png"},
                }},
            }},
            { type = "grid", id = "main_grid", position = {0, 3200}, columns = 2, column_width = 180, row_height = 50, focusable = true, sorted = true},
            { type = "view", id = "base.ui.right_panel.sub_controller", position = {0, 150}, size = {350, 600}, background = 0x203636ff, special = true},
        }},
    },

    on_game_start = function(view)
        if application.game then
            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. application.game:getSpeed() .. ".png");
        end
    end,
})
