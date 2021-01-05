ui:extend({
    type = "view",
    id = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.MainPanelController",
    align = {"top", "right"},
    position = {0, 38},
    background = blue_dark_4,
    size = {377, application.screen_height},
    views = {
        {
            type = "view", size = {4, application.screen_height}, background = blue_dark_1
        },

        {
        type = "list",
        position = {16, 3},
        id = "content_list",
        views = {

            { type = "view", size = {348, 307}, position = {0, 10}, background = blue_light_1, views = {

                { type = "view", id = "map_header", position = {1, 1}, size = {346, 44}, background = blue_dark_3, views = {
                    { type = "label", id = "lb_planet", text_size = 16, text_color = blue_light_5, padding = 10 },
                    { type = "label", id = "lb_floor", text_size = 16, text_color = blue_light_5, padding = 10, position = {250, 0} },
                }},
                { type = "view", id = "map_container", position = {1, 40}, size = {346, 228}, background = blue_dark_3, views = {
                    { type = "minimap"},
                    { type = "image", id = "img_speed", position = {316, 205}, size = {32, 32}},
                }},
                { type = "view", id = "map_footer", position = {1, 268}, size = {346, 38}, background = blue_dark_3, views = {
                    { type = "grid", columns = 20, column_width = 30, row_height = 38, size = {346, 34}, views = {
                        { type = "label", text = "A", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_areas", on_click = function(v) application:toggleDisplay("areas") end},
                        { type = "label", text = "R", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_rooms", on_click = function(v) application:toggleDisplay("rooms") end},
                        { type = "label", text = "T", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_temperature", on_click = function(v) application:toggleDisplay("temperature") end},
                        { type = "label", text = "O", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_oxygen", on_click = function(v) application:toggleDisplay("oxygen") end},
                        { type = "label", text = "W", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_water", on_click = function(v) application:toggleDisplay("water") end},
                        { type = "label", text = "S", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_security", on_click = function(v) application:toggleDisplay("security") end},
                    }},
                }},

            }},

            { type = "view", size = {300, 40}, position = {0, 20}, views = {
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
            { type = "view", id = "base.ui.right_panel.sub_controller", position = {0, 0}, size = {350, 600}, special = true},
        }},
    },

    on_game_start = function(view)
        if application.game then
            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. application.game:getSpeed() .. ".png");
        end
    end,
})
