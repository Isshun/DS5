local gauge_spacing = 24;
local gauge_width = 18;

ui:extend({
    type = "view",
    id = "base.ui.right_panel.crew",
    debug = true,
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.CrewController",
    visible = false,
    views = {
        { type = "view", views = {
--            { type = "view", position = {270, 10}, views = {
--                {type = "image", src = "[base]/graphics/needs/ic_food.png", size = {12, 12}, position = {gauge_spacing * 0, 0}},
--                {type = "image", src = "[base]/graphics/needs/ic_health.png", size = {12, 12}, position = {gauge_spacing * 1, 0}},
--                {type = "image", src = "[base]/graphics/needs/ic_social.png", size = {12, 12}, position = {gauge_spacing * 2, 0}},
--                {type = "image", src = "[base]/graphics/needs/ic_entertainment.png", size = {12, 12}, position = {gauge_spacing * 3, 0}},
--            }},
--
--            { type = "list", position = {20, 30}, spacing = 10, views = {
--                { type = "label", text = "1 NICOLAS MARTIN", text_size = 14, text_font = "square", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "2 NICOLAS MARTIN", text_size = 14, text_font = "avant", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "3 Nicolas MARTIN", text_size = 14, text_font = "sf", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "4 NICOLAS MARTIN", text_size = 14, text_font = "silly", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "5 NICOLAS MARTIN", text_size = 14, text_font = "pixel_mix", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "6 NICOLAS MARTIN", text_size = 14, text_font = "splitter", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "7 NICOLAS MARTIN", text_size = 14, text_font = "splitter", text_color = yellow, size = {100, 30}, position = {0, 10}},
--                { type = "label", text = "8 NICOLAS MARTIN", text_size = 14, text_font = "pixelade", text_color = yellow, size = {100, 30}, position = {0, 10}},
--            }},

--            0x67c706ff
--            0x0cb797ff
--            0xf8554cff
--            0x42ad20ff
--            0x0cb797ff
            { type = "list", id = "list_crew", position = {10, 10}, spacing = 10, template = {
                { type = "view", size = {370, 55}, background = blue_dark_3, views = {
--                    { type = "label", id = "lb_character_name", text_font = "sf", outlined = true, text_color = 0xffb324ff, text_size = 20, size = {300, 28}, position = {8, 10}},
--                    { type = "label", id = "lb_character_skill", text_font = "sf", outlined = true, background = 0xffb324ff, text_size = 12, position = {8, 33}, padding = {1, 1}, text = "CRAFTER"},
--                    { type = "label", id = "lb_character_job", text_font = "sf", outlined = true, text_color = 0xffb324ff, text_size = 12, size = {300, 28}, position = {8, 28}, padding = {8, 0}, text = "USE WOOD BED"},

                    { type = "label", id = "lb_character_name", text_font = "font3", outlined = false, text_color = 0xffb324ff, text_size = 18, size = {300, 28}, position = {8, 12}},
                    { type = "label", id = "lb_character_skill", visible = false, text_font = "sui", outlined = true, background = 0xffb324ff, text_size = 12, position = {8, 33}, padding = {1, 1}, text = "CRAFTER"},
                    { type = "label", id = "lb_character_job", text_font = "sui", outlined = true, text_color = 0xffb324ff, text_size = 10, size = {300, 28}, position = {8, 28}, padding = {8, 0}, text = "USE WOOD BED"},

                    { type = "grid", columns = 4, column_width = gauge_spacing, row_height = gauge_spacing, position = {272, 7}, views = {

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