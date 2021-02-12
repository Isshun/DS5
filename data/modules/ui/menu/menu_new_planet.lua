local max_width = 1360;
local max_height = 768;
local window_border_size = 4;
local left_pane_width = max_width * 0.25;
local right_pane_width = max_width * 0.75;

ui:extend({
    type = "view",
    id = "base.ui.menu.new_planet",
    controller = "org.smallbox.faraway.client.controller.menu.MenuPlanetController",
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
        },
    },
    views = {

        --        { type = "view", background = 0x080808ff, size = {1920, 1080}, position = {application.screen_width / 2 - 1920 / 2, application.screen_height / 2 - 1080 / 2}},
        --        { type = "view", background = 0x111111ff, size = {1360, 768}, position = {application.screen_width / 2 - 1360 / 2, application.screen_height / 2 - 768 / 2}},
        --        { type = "view", background = 0x222222ff, size = {1280, 720}, position = {application.screen_width / 2 - 1280 / 2, application.screen_height / 2 - 720 / 2}},

        { type = "view", background = blue_light_1,
            size = {max_width + window_border_size * 2, max_height + window_border_size * 2},
            position = {application.screen_width / 2 - max_width / 2 - window_border_size, application.screen_height / 2 - max_height / 2 - window_border_size }
        },

        -- Inner view
        { type = "view", size = {max_width, max_height}, position = {application.screen_width / 2 - max_width / 2, application.screen_height / 2 - max_height / 2}, views = {

            -- Left pane
            { type = "view", background = blue_dark_4, size = {left_pane_width, max_height}, position = {0, 0}, views = {
                { type = "list", id = "list_planets", template = {
                    { type = "label", id = "lb_planet", text_font = "font3", text_color = 0x000000ff, text_focus_color = yellow, text_size = 24, padding = 20, size = {300, 42}},
                }},
                { type = "label", text = "Back", style = "action_button", position = {20, max_height - 60}, text_align = "LEFT", action = "onActionBack"},
            }},

            -- Right pane
            { type = "view", background = blue_dark_3, size = {right_pane_width, max_height}, position = {left_pane_width, 0}, views = {

                { type = "list", id = "info_planet", position = {20, 0}, views = {
                    { type = "label", id = "lb_info_name", style = "h1"},

                    -- Regions
                    { type = "list", id = "list_info_regions", spacing = 20, template = {
                        { type = "view", size = {1000, 300}, background = blue_dark_2, views = {
                            { type = "list", size = {1000, 300}, position = {18, 8}, spacing = 10, views = {
                                { type = "label", id = "lb_region_name", style = "h2"},

                                -- Biome
                                { type = "label", text = "Biome", style = "h3"},
                                { type = "grid", columns = 2, column_width = 200, row_height = 30, size = {400, 400}, views = {
                                    { type = "view", views = {
                                        { type = "image", src = "[base]/graphics/icons/hostility.png", size = {24, 24}},
                                        { type = "label", text_font = "sui", id = "lb_hostility", text_color = 0xffffffcc, text_size = 16, padding = 7, position = {30, 0}},
                                    }},
                                    { type = "view", views = {
                                        { type = "image", src = "[base]/graphics/icons/fertility.png", size = {24, 24}},
                                        { type = "label", text_font = "sui", id = "lb_fertility", text_color = 0xffffffcc, text_size = 16, padding = 7, position = {30, 0}},
                                    }},
                                    { type = "view", views = {
                                        { type = "image", src = "[base]/graphics/icons/temperature.png", size = {24, 24}},
                                        { type = "label", text_font = "sui", id = "lb_temperature", text_color = 0xffffffcc, text_size = 16, padding = 7, position = {30, 0}},
                                    }},
                                    { type = "view", views = {
                                        { type = "image", src = "[base]/graphics/icons/o2.png", size = {24, 24}},
                                        { type = "label", text_font = "sui", id = "lb_atmosphere", text_color = 0xffffffcc, text_size = 16, padding = 7, position = {30, 0}},
                                    }},
                                }},

                                -- Resources
                                { type = "label", text = "Resources", style = "h3"},
                                { type = "grid", id = "grid_info_resources", columns = 3, column_width = 160, row_height = 30, template = {
                                    { type = "view", size = {120, 30}, views = {
                                        { type = "image", id = "img_resource", background = red, size = {24, 24}},
                                        { type = "label", id = "lb_resource", text_font = "sui", text_align = "LEFT", text_color = 0xffffffcd, size = {24, 24}, position = {36, 0}},
                                    }}
                                }},

                                { type = "view", size = {0, 30}}
                            }},
                        }},
                    }},
                }},

                { type = "image", id = "img_planet", position = {right_pane_width / 2, 60}, size = {right_pane_width / 2, max_height - 60}},
                { type = "label", text = "Embark", style = "action_button", position = {right_pane_width - 260, max_height - 60}, text_align = "RIGHT", action = "onActionNext"},
            }},

        }}

    },

})
