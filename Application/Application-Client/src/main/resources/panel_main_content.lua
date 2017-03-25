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
            { type = "view", id = "map_header", position = {0, 12}, size = {352, 34}, background = 0x203636, views = {
                { type = "label", id = "lb_planet", text_size = 16, text_color = color2, padding = 10 },
                { type = "label", id = "lb_floor", text_size = 16, text_color = color2, padding = 10, position = {250, 0} },
            }},
            { type = "view", id = "map_container", size = {352, 230}, views = {
                { type = "minimap"},
                { type = "image", id = "img_speed", position = {316, 205}, size = {32, 32}},
            }},
            { type = "view", id = "map_footer", size = {352, 38}, views = {
                { type = "grid", columns = 20, column_width = 30, row_height = 38, background = 0x203636, size = {352, 34}, views = {
                    { type = "label", text = "A", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_areas", on_click = function(v) application:toggleDisplay("areas") end},
                    { type = "label", text = "R", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_rooms", on_click = function(v) application:toggleDisplay("rooms") end},
                    { type = "label", text = "T", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_temperature", on_click = function(v) application:toggleDisplay("temperature") end},
                    { type = "label", text = "O", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_oxygen", on_click = function(v) application:toggleDisplay("oxygen") end},
                    { type = "label", text = "W", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_water", on_click = function(v) application:toggleDisplay("water") end},
                    { type = "label", text = "S", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_security", on_click = function(v) application:toggleDisplay("security") end},
                }},
            }},
            { type = "grid", id = "main_grid", position = {0, 32}, columns = 2, column_width = 180, row_height = 50, focusable = true, sorted = true},
        }},
--        {
--            type = "view",
--            position = {0, application.info.screen_height - 150},
--            views = {
--                { type = "label", id = "lb_ground", text_size = 14},
--                { type = "grid", columns = 10, column_width = 58, row_height = 100, position = {10, 10}, views = {
--                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
--                        { type = "image", id = "thumb_o2", src = "[base]/graphics/icons/thumb_o2.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
--                        { type = "label", id = "lb_oxygen", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
--                    }},
--
--                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
--                        { type = "image", id = "thumb_walkable", src = "[base]/graphics/icons/thumb_walkable.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
--                        { type = "label", id = "lb_walkable", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
--                    }},
--
--                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
--                        { type = "image", id = "thumb_water", src = "[base]/graphics/icons/thumb_water.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
--                        { type = "label", id = "lb_water", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
--                    }},
--
--                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
--                        { type = "image", id = "thumb_temperature", src = "[base]/graphics/icons/thumb_temperature.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
--                        { type = "label", id = "lb_temperature", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
--                    }},
--
--                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
--                        { type = "image", id = "thumb_temperature", src = "[base]/graphics/icons/thumb_light.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
--                        { type = "label", id = "lb_light", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
--                    }},
--
--                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
--                        { type = "image", id = "thumb_inside", src = "[base]/graphics/icons/thumb_home.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
--                        { type = "label", id = "lb_inside", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
--                    }},
--                }}
--            }}
    },

    on_game_start = function(view)
        if application.game then
            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. application.game:getSpeed() .. ".png");
        end
    end,
})
