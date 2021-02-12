local max_width = 1360;
local max_height = 768;
local window_border_size = 4;
local left_pane_width = max_width / 3;
local center_pane_width = max_width / 3;
local right_pane_width = max_width / 3;

ui:extend({
    type = "view",
    id = "base.ui.menu.load_game",
    controller = "org.smallbox.faraway.client.controller.menu.MenuLoadController",
    size = {application.screen_width, application.screen_height},
    in_game = false,
    visible = false,
    styles = {
        {
            id = "h1",
            text_font = "font3",
            text_color = blue_light_3,
            text_size = 60,
            text_align = "LEFT",
            size = {300, 100},
        }, {
            id = "h2",
            padding = {10, 0, 0, 0},
            text_font = "font3",
            text_color = blue_light_2,
            text_size = 24,
        }, {
            id = "h3",
            text_font = "sui",
            text_color = 0xffffffdd,
            text_size = 20,
            size = {300, 50},
            padding = {10, 0, 0, 0},
            text_align = "LEFT",
        }, {
            id = "label",
            text_font = "sui",
            text_color = blue_light_1,
            text_size = 16,
            size = {max_width / 3, 42},
            padding = {15, 15}
        }, {
            id = "entry",
            text_font = "sui",
            text_color = 0xffffffc4,
            text_size = 20,
            size = {max_width / 3, 42},
            padding = {15, 15}
        }
    },
    views = {

        { type = "view", background = blue_light_1,
            size = {max_width + window_border_size * 2, max_height + window_border_size * 2},
            position = {application.screen_width / 2 - max_width / 2 - window_border_size, application.screen_height / 2 - max_height / 2 - window_border_size }
        },

        -- Inner view
        { type = "view", size = {max_width, max_height}, position = {application.screen_width / 2 - max_width / 2, application.screen_height / 2 - max_height / 2}, views = {

            -- Left pane
            { type = "view", background = blue_dark_4, size = {left_pane_width, max_height}, position = {0, 0}, views = {
                { type = "list", id = "list_games", template = {
                    { type = "label", id = "lb_game", style = "entry", text_focus_color = yellow },
                }},
                { type = "label", text = "Back", style = "action_button", position = {20, max_height - 60}, text_align = "LEFT", action = "onActionBack"},
            }},

            -- Center pane
            { type = "view", background = blue_dark_3, size = {center_pane_width, max_height}, position = {left_pane_width, 0}, views = {
                { type = "view", size = {2, fill}, background = black},

                {type = "view", id = "game_detail", visible = false, size = {right_pane_width, 122}, background = 0x00000044, views = {
                    -- Planet
                    { type = "label", text = "Planet", style = "label", text_focus_color = yellow },
                    { type = "label", id = "lb_game_planet", style = "entry", text_focus_color = yellow, position = {0, 20} },

                    -- Region
                    { type = "label", text = "Region", style = "label", text_focus_color = yellow, position = {0, 55} },
                    { type = "label", id = "lb_game_region", style = "entry", text_focus_color = yellow, position = {0, 75} },

                    -- Size
                    { type = "label", text = "Size", style = "label", text_focus_color = yellow, position = {200, 0} },
                    { type = "label", id = "lb_game_size", style = "entry", text_focus_color = yellow, position = {200, 20} },

                    -- Reserved
                    { type = "label", text = "Reserved", style = "label", text_focus_color = yellow, position = {200, 55} },
                    { type = "label", text = "Reserved", id = "lb_game_reserved", style = "entry", text_focus_color = yellow, position = {200, 75} },
                }},

                { type = "view", size = {fill, 2}, position = {0, 122}, background = black},

                { type = "list", id = "list_saves", position = {0, 124}, template = {
                    { type = "label", id = "lb_save", style = "entry", text_focus_color = yellow },
                }},

            }},

            -- Right pane
            { type = "view", background = blue_dark_2, size = {right_pane_width, max_height}, position = {left_pane_width + center_pane_width, 0}, views = {
                { type = "view", size = {2, fill}, background = black},

                {type = "view", id = "load_detail", visible = false, size = {right_pane_width, 240}, position = {15, 15}, views = {
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
                }},

                { type = "label", text = "Load", style = "action_button", position = {right_pane_width - 260, max_height - 60}, text_align = "RIGHT", action = "onActionLoad"},
            }},

        }}

    },

})
