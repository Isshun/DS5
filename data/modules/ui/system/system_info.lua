local window_width = 400
local minimap_header_height = 52
local minimap_container_height = window_width * 0.60
local minimap_footer_height = 0
local window_height = minimap_header_height + minimap_container_height + minimap_footer_height

ui:extend({
    type = "view",
    id = "base.ui.game_info",
    size = {window_width, application.screen_height},
    controller = "org.smallbox.faraway.client.controller.SystemInfoController",
    align = {"top", "right"},
    position = {10, 10},
    level = 100,
    visible = true,
    views = {

        { type = "view", size = {window_width, window_height}, background = blue_dark_3, views = {

            -- HEADER
            { type = "view", id = "map_header", position = {1, 1}, size = {window_width - 2, minimap_header_height - 2}, views = {

                -- Top right system icons
                { type = "view", id = "view_weather", views = {

                    -- Time and day
                    { type = "image", id = "img_time", src = "[base]/graphics/icons/daytimes/noon.png", size = {32, 32}, position = {2000, 2}},
                    { type = "image", src = "[base]/graphics/icons/daytimes/sun.png", size = {minimap_header_height, minimaCp_header_height}},
                    { type = "label", id = "lb_time", text = "lb_time", text_font = "font3", text_color = 0xffffffcc, text_size = 18, position = {52, 10} },
                    { type = "label", id = "lb_date", text = "lb_date", text_font = "font3", text_color = 0xffffff88, text_size = 13, position = {52, 31} },

                    -- Weather
                    { type = "label", id = "lb_weather", text = "lb_weather", text_font = "font3", text_color = 0xffffffcc, text_size = 18, text_align = "RIGHT", text_length = 20, size = {200, 20}, position = {window_width - 280, 10}},
                    { type = "label", id = "lb_temperature", text = "lb_temperature", text_font = "font3", text_color = 0xffffff88, text_size = 13, text_align = "RIGHT", text_length = 26, size = {200, 20}, position = {window_width - 280, 31}},
                    { type = "image", id = "img_weather", src = "[base]/graphics/icons/weather/regular.png", size = {32, 32}, position = {window_width - 40, 3}},

                    -- Game speed
                    { type = "image", id = "ic_speed", src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {220, 4}},
                    { type = "label", id = "lb_speed", text_color = blue_light_5, text_size = 16, position = {220, 4}},

                    -- Menu icon
                    --            { type = "image", src = "[base]/graphics/icons/menu.png", position = {335, 4}, size = {32, 32}, action = "onActionMenu"},
                }},

            }},
            -- HEADER: END

            -- MAP
            { type = "view", id = "map_container", position = {1, minimap_header_height}, size = {window_width - 2, minimap_container_height}, background = blue_dark_5, views = {
                { type = "minimap", id = "minimap"},
                { type = "image", id = "img_speed", position = {40, minimap_container_height - 40}, size = {32, 32}},
                { type = "label", id = "lb_floor", text_size = 16, text_color = white, text_outlined = true, padding = 10, position = {window_width - 30, minimap_container_height - 30}},
            }},
            -- MAP: END

--            -- FOOTER
--            { type = "view", id = "map_footer", position = {1, window_height - minimap_footer_height - 1}, size = {window_width - 2, minimap_footer_height}, background = blue_dark_3, views = {
--                { type = "grid", columns = 20, column_width = 30, row_height = minimap_footer_height, size = {346, minimap_footer_height}, views = {
--                    { type = "label", text = "A", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_areas", on_click = function(v) application:toggleDisplay("areas") end},
--                    { type = "label", text = "R", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_rooms", on_click = function(v) application:toggleDisplay("rooms") end},
--                    { type = "label", text = "T", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_temperature", on_click = function(v) application:toggleDisplay("temperature") end},
--                    { type = "label", text = "O", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_oxygen", on_click = function(v) application:toggleDisplay("oxygen") end},
--                    { type = "label", text = "W", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_water", on_click = function(v) application:toggleDisplay("water") end},
--                    { type = "label", text = "S", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636ff, focus = 0x203636ff}, id = "lb_display_security", on_click = function(v) application:toggleDisplay("security") end},
--                }},
--            }},
--            -- FOOTER: END

        }},

    },

})