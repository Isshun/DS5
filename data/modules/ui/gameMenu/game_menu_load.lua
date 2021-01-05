local window_width = 1200
local window_height = 800
local window_postion_x = application.screen_width / 2 - window_width / 2
local window_postion_y = application.screen_height / 2 - window_height / 2

ui:extend({
    type = "view",
    id = "base.ui.game_menu.load",
    size = {application.screen_width, application.screen_height},
    controller = "org.smallbox.faraway.client.controller.gameMenu.GameMenuLoadController",
    visible = false,
    level = 100,
    views = {

        -- Pause frame
        { type = "view", id = "view_load", background = 0x00000055, size = {application.screen_width, application.screen_height}, views = {
            { type = "view", background = 0x002255ff, border = blue_light_2, position = {window_postion_x, window_postion_y}, size = {window_width, window_height}, views = {
                {type = "list", id = "load_entries", position = {10, 14}},
                {type = "view", background = blue_light_3, size = {1, window_height}, position = {850, 0}},
                {type = "view", id = "load_detail", visible = false, size = {318, 240}, position = {866, 16}, views = {
                    {type = "view", id = "image_detail", background = 0x000000ff, size = {318, 240}},
                    {type = "list", position = {0, 260}, views = {
                        { type = "label", text_color = blue_light_1, text = "Name", text_size = 16},
                        { type = "label", text_color = blue_light_4, margin = {0, 0, 10, 0}, id = "lb_detail_name", text_size = 20},
                        { type = "label", text_color = blue_light_1, text = "Duration", text_size = 16},
                        { type = "label", text_color = blue_light_4, margin = {0, 0, 10, 0}, id = "lb_detail_duration", text_size = 20},
                        { type = "label", text_color = blue_light_1, text = "Real duration", text_size = 16},
                        { type = "label", text_color = blue_light_4, margin = {0, 0, 10, 0}, id = "lb_detail_real_duration", text_size = 20},
                        { type = "label", text_color = blue_light_1, text = "Crew", text_size = 16},
                        { type = "label", text_color = blue_light_4, margin = {0, 0, 10, 0}, id = "lb_detail_crew", text_size = 20},
                    }},
                    { type = "label", size = {308, 50}, position = {0, window_height - 90}, background = blue_light_2, margin = {5, 0, 0, 5}, text_size = 20, text_align = "center", text = "Load", action="onActionLoad"},
                }},
            }}
        }},
--
--         { type = "label", text = "Stand-by", text_size = 42, text_color = 0x25c9cbff, position = {20, application.screen_height - 52}},
--         { type = "view", id = "bt_cursor", background = 0x25c9cbff, size = {20, 32}, position = {210, application.screen_height - 52}},
--         { type = "list", background = 0xffff00ff, position = {application.screen_width / 2 - 280 / 2, application.screen_height / 2 - 240 / 2}, views = {
--             { type = "label", style = "menu.pause.button", action = "onResume", text = "Resume"},
--             { type = "label", style = "menu.pause.button", action = "onSave", text = "Save"},
--             { type = "label", style = "menu.pause.button", action = "onLoad", text = "Load"},
--             { type = "label", style = "menu.pause.button", action = "onSettings", text = "Settings"},
--             { type = "label", style = "menu.pause.button", action = "onQuit", text = "Quit"},
--         }},
    },
})