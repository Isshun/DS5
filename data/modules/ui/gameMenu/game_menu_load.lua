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
    styles = {
        {
            id = "action_button",
            text_font = "font3",
            text_size = 38,
            text_color = blue_light_1,
            text_focus_color = yellow,
            shadow = 3,
            shadow_color = 0x000000aa,
            size = {240, 50},
        },
    },
    views = {

        -- Pause frame
        { type = "view", id = "view_load", background = 0x00000055, size = {application.screen_width, application.screen_height}, views = {
            { type = "view", background = blue_dark_4, border = blue_light_1, border_size = 4, position = {window_postion_x, window_postion_y}, size = {window_width, window_height}, views = {

                -- Left pane
                {type = "list", id = "load_entries", position = {18, 18}, spacing = 16, template = {
                    { type = "label", text_color = blue_light_4, background = 0xffffff22, focus = 0xffffff55, size = {716, 60}, text_align = "LEFT", padding = {0, 0, 0, 18}, id = "lb_entry", text_size = 20, text_font = "sui"},
                }},

                -- Separator
                {type = "view", background = blue_light_1, size = {4, window_height}, position = {750, 0}},

                -- Right pane
                {type = "view", id = "load_detail", visible = false, size = {418, 240}, position = {766, 16}, views = {
                    {type = "image", id = "img_detail", background = 0x000000ff, size = {418, 280}},
                    {type = "list", position = {0, 294}, spacing = 10, views = {
                        { type = "label", text_font = "sui", text_color = blue_light_1, text = "Name", text_size = 16},
                        { type = "label", text_font = "sui", text_color = 0xffffffc4, margin = {0, 0, 10, 0}, id = "lb_detail_name", text_size = 20},
                        { type = "label", text_font = "sui", text_color = blue_light_1, text = "Duration", text_size = 16},
                        { type = "label", text_font = "sui", text_color = 0xffffffc4, margin = {0, 0, 10, 0}, id = "lb_detail_duration", text_size = 20},
                        { type = "label", text_font = "sui", text_color = blue_light_1, text = "Real duration", text_size = 16},
                        { type = "label", text_font = "sui", text_color = 0xffffffc4, margin = {0, 0, 10, 0}, id = "lb_detail_real_duration", text_size = 20},
                        { type = "label", text_font = "sui", text_color = blue_light_1, text = "Crew", text_size = 16},
                        { type = "label", text_font = "sui", text_color = 0xffffffc4, margin = {0, 0, 10, 0}, id = "lb_detail_crew", text_size = 20},
                    }},
                    { type = "label", size = {408, 50}, position = {0, window_height - 90}, style = "action_button", text_align = "CENTER", text = "Load", action="onActionLoad"},
                }},

            }}

        }},
    },
})