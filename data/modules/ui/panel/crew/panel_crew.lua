local window_width = 400
local window_pos = 346
local button_number = 4
local button_width = window_width / 4
local button_height = 52
local gauge_spacing = 24;
local gauge_width = 18;

ui:extend({
    type = "view",
    id = "base.ui.right_panel.crew",
    debug = true,
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.CrewController",
    visible = false,
    background = yellow,
    views = {

        { type = "view", size = {panel_width - 8, application.screen_height - window_pos - button_height - 10 - 8}, position = {4, 4}, background = blue_dark_4, views = {
            { type = "list", id = "list_crew", position = {10, 10}, spacing = 10, template = {
                { type = "view", size = {panel_width - 30, 55}, background = blue_dark_3, views = {
                    { type = "label", id = "lb_character_name", text_font = "font3", outlined = false, text_color = 0xffb324ff, text_size = 18, size = {300, 28}, position = {8, 12}},
                    { type = "label", id = "lb_character_skill", visible = false, text_font = "sui", outlined = true, background = 0xffb324ff, text_size = 12, position = {8, 33}, padding = {1, 1}, text = ""},
                    { type = "label", id = "lb_character_job", text_font = "sui", outlined = true, text_color = 0xffb324ff, text_size = 10, size = {300, 28}, position = {8, 28}, padding = {8, 0}, text = ""},

                    { type = "grid", columns = 4, column_width = gauge_spacing, row_height = gauge_spacing, position = {panel_width - 128, 7}, views = {

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", id = "gauge_food", size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_health_black.png", size = {18, 18}},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", id = "gauge_health", size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_hearth_black.png", size = {18, 18}},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", id = "gauge_social", size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_power_black.png", size = {18, 18}},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", id = "gauge_social", size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_food_black.png", size = {18, 18}},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", position = {0, 0}, size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            { type = "label", id = "ic_skill", text_font = "font3", outlined = false, text_color = 0x00000088, text_size = 18, padding = {3, 2}, text = "G"},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", position = {0, 0}, size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_stress_black.png", size = {18, 18}},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", position = {0, 0}, size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_oxygen_black.png", size = {18, 18}},
                        }},

                        { type = "view", position = {0, 0}, views = {
                            {type = "view", size = {gauge_width, gauge_width}, background = blue_dark_5},
                            {type = "view", position = {0, 0}, size = {gauge_width, gauge_width}, background = 0x42ad20ff},
                            {type = "image", src = "[base]/graphics/icons/ic_drop_black.png", size = {18, 18}},
                        }},

                    }},

                }},

            }},

        }}
    },

})