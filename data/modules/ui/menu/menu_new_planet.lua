local max_width = 1360;
local max_height = 768;
local window_border_size = 4;
local left_pane_width = max_width * 0.25;
local right_pane_width = max_width * 0.75;

ui:extend({
    type = "view",
    id = "base.ui.menu.new_planet",
    controller = "org.smallbox.faraway.client.menu.controller.MenuPlanetController",
    size = {application.screen_width, application.screen_height},
    in_game = false,
    visible = false,
    views = {
        { type = "image", src = "[base]/background/17520.jpg", size = {application.screen_width, application.screen_height}},

--        { type = "view", background = 0x080808ff, size = {1920, 1080}, position = {application.screen_width / 2 - 1920 / 2, application.screen_height / 2 - 1080 / 2}},
--        { type = "view", background = 0x111111ff, size = {1360, 768}, position = {application.screen_width / 2 - 1360 / 2, application.screen_height / 2 - 768 / 2}},
--        { type = "view", background = 0x222222ff, size = {1280, 720}, position = {application.screen_width / 2 - 1280 / 2, application.screen_height / 2 - 720 / 2}},

        { type = "view", background = blue_light_1,
            size = {max_width + window_border_size * 2, max_height + window_border_size * 2},
            position = {application.screen_width / 2 - max_width / 2 - window_border_size, application.screen_height / 2 - max_height / 2 - window_border_size }
        },

        -- Inner view
        { type = "view", size = {max_width, max_height}, position = {application.screen_width / 2 - max_width / 2, application.screen_height / 2 - max_height / 2}, views = {

            { type = "view", background = blue_dark_4, size = {left_pane_width, max_height}, position = {0, 0}, views = {
                { type = "list", id = "list_planets"},
--                { type = "label", id = "bt_back", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "back", position = {0, 650}, text_size = 22, size = {100, 40}, action = "onActionBack"},
--                { type = "label", id = "bt_next", padding = 10, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, text = "next", position = {200, 650}, text_size = 22, size = {100, 40}, action = "onActionNext"},
            }},

            { type = "view", background = blue_dark_3, size = {right_pane_width, max_height}, position = {left_pane_width, 0}, views = {

                { type = "list", id = "info_planet", position = {20, 0}, views = {
                    { type = "label", id = "lb_info_name", text_color = blue_light_3, text_size = 60, padding = {20, 0, 10, 0}},
                    { type = "label", text = "Biomes", text_color = blue_light_3, text_size = 30, padding = {0, 0, 10, 0}},
                    { type = "list", id = "list_info_regions"},
                }},

                { type = "image", id = "img_planet", position = {right_pane_width / 2, 60}, size = {right_pane_width / 2, max_height - 60}},

            }},

        }}

    },

})
