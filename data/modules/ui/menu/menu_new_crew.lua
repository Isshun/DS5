local max_width = 1360;
local max_height = 768;
local window_border_size = 4;
local left_pane_width = max_width * 0.25;
local right_pane_width = max_width * 0.75;

ui:extend({
    type = "view",
    id = "base.ui.menu.new_crew",
    controller = "org.smallbox.faraway.client.menu.controller.MenuCrewController",
    size = {application.screen_width, application.screen_height},
    in_game = false,
    visible = false,
    views = {
        { type = "image", src = "[base]/background/17520.jpg", size = {application.screen_width, application.screen_height}},

        { type = "view", background = blue_light_1,
            size = {max_width + window_border_size * 2, max_height + window_border_size * 2},
            position = {application.screen_width / 2 - max_width / 2 - window_border_size, application.screen_height / 2 - max_height / 2 - window_border_size }
        },

        -- Inner view
        { type = "view", size = {max_width, max_height}, position = {application.screen_width / 2 - max_width / 2, application.screen_height / 2 - max_height / 2}, views = {

            { type = "view", background = blue_dark_4, size = {left_pane_width, max_height}, position = {0, 0}, views = {
                { type = "list", id = "list_planets"},

                { type = "label", id = "bt_back", text = "Back", padding = 16, text_size = 22,
                    background = {regular = 0x55ffffff, focus = 0x8814dcb9},
                    position = {10, max_height - 60},
                    size = {140, 50},
                    action = "onActionBack"
                },

            }},

            { type = "view", background = blue_dark_3, size = {right_pane_width, max_height}, position = {left_pane_width, 0}, views = {
                { type = "label", text = "Crew", text_size = 38},
                { type = "list", id = "list_crew", position = {0, 40}},

                { type = "image", id = "img_planet", position = {right_pane_width / 2, 60}, size = {right_pane_width / 2, max_height - 60}},
                { type = "label", id = "bt_next", text = "Embark", padding = 16, text_size = 22,
                    background = {regular = 0x55ffffff, focus = 0x8814dcb9},
                    position = {right_pane_width - 150, max_height - 60},
                    size = {140, 50},
                    action = "onActionNext"
                },
            }},

        }},

    },

})