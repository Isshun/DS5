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
            { type = "view", id = "map_header", position = {0, 12}, size = {352, 34}, background = 0x203636ff, views = {
                { type = "label", id = "lb_planet", text_size = 16, text_color = color2, padding = 10 },
                { type = "label", id = "lb_floor", text_size = 16, text_color = color2, padding = 10, position = {250, 0} },
            }},
            { type = "view", id = "map_container", size = {352, 230}, views = {
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
            { type = "grid", id = "main_grid", position = {0, 32}, columns = 2, column_width = 180, row_height = 50, focusable = true, sorted = true},
        }},
    },

    on_game_start = function(view)
        if application.game then
            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. application.game:getSpeed() .. ".png");
        end
    end,
})
