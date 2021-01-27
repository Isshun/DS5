consumable = nil

ui:extend({
    type = "view",
    id = "base.ui.info_plant",
    parent = "base.ui.right_panel.sub_controller_full",
    controller = "org.smallbox.faraway.client.controller.PlantInfoController",
    visible = false,
    size = {400, 940},
    views = {

        { type = "view", background = blue_dark_3, views = {
            { type = "view", size = {400 - 6, 940 - 6}, background = 0x181818ff, position = {3, 3}, views = {
                { type = "view", size = {392, 932}, position = {1, 1}, background = 0x262626ff, views = {
                    { type = "label", id = "lb_position", position = {200, 0}, text_color = 0x357f7fff, text_size = 12, margin = {9, 12, 0, 12}, position = {270, 0}},

                    { type = "view", views = {
                        { type = "list", position = {1, 0}, views = {

                            { type = "view", size = {fill, 70}, views = {
                                --                        { type = "label", text = "Ground", text_font = "font3", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                                { type = "label", id = "lb_name", text_font = "font3", text = "ground", text_size = 26, text_color = 0xb4b4b4ff, margin = {20, 16} },
                                { type = "label", action = "onClose", text = "x", text_font = "whitrabt", outlined = false, text_color = 0x181818ff, background = 0xb4b4b4ff, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {349, 15}},
                                { type = "view", size = {360, 1}, position = {15, 59}, background = 0xb4b4b4ff},
                                { type = "view", size = {120, 120}, position = {255, 78}, background = 0xb4b4b4ff},

                                { type = "list", position = {15, 80}, views = {
                                    { type = "label", id = "lb_maturity", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 16, size = {300, 25}},
                                    { type = "label", id = "lb_garden", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 16, size = {300, 25}},
                                    { type = "label", id = "lb_seed", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 16, size = {300, 25}},
                                    { type = "label", id = "lb_nourish", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 16, size = {300, 25}},
                                    { type = "label", id = "lb_job", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 16, size = {300, 25}},
                                    { type = "label", id = "lb_growing", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 16, size = {300, 25}},

                                    -- Environnment
                                    { type = "label", text = "Environnment", size = {300, 60}, text_align = "LEFT", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 18},
                                    { type = "grid", columns = 2, column_width = 195, row_height = 60, views = {

                                        { type = "view", size = {185, 50}, views = {
                                            { type = "label", text = "Temperature", text_font = "sui", text_color = blue_light_1, text_size = 16 },
                                            { type = "image", src = "[base]/graphics/icons/plant_cursor.png", size = {128, 8}, position = {0, 18} },
                                            { type = "label", id = "lb_current_temperature", text_color = blue_light_2, text_size = 12 },
                                            { type = "view", id = "img_temperature", size = {6, 12}, background = 0xffffffff },
                                            { type = "label", id = "lb_temperature", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 8, size = {300, 25}},
                                        }},

                                        { type = "view", size = {185, 50}, views = {
                                            { type = "label", text = "Light", text_font = "sui", text_color = blue_light_1, text_size = 16 },
                                            { type = "image", src = "[base]/graphics/icons/plant_cursor_light.png", size = {128, 8}, position = {0, 18} },
                                            { type = "label", id = "lb_current_light", text_color = blue_light_2, text_size = 12 },
                                            { type = "view", id = "img_light", size = {6, 12}, background = 0xffffffff},
                                            { type = "label", id = "lb_light", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 8, size = {300, 25}},
                                        }},

                                        { type = "view", size = {185, 50}, views = {
                                            { type = "label", text = "Moisture", text_font = "sui", text_color = blue_light_1, text_size = 16 },
                                            { type = "image", src = "[base]/graphics/icons/plant_cursor_moisture.png", size = {128, 8}, position = {0, 18} },
                                            { type = "label", id = "lb_current_moisture", text_color = blue_light_2, text_size = 12 },
                                            { type = "view", id = "img_moisture", size = {6, 12}, background = 0xffffffff },
                                            { type = "label", id = "lb_moisture", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 8, size = {300, 25}},
                                        }},

                                        { type = "view", size = {185, 50}, views = {
                                            { type = "label", text = "Oxygen", text_font = "sui", text_color = blue_light_1, text_size = 16 },
                                            { type = "image", src = "[base]/graphics/icons/plant_cursor_oxygen.png", size = {128, 8}, position = {0, 18} },
                                            { type = "label", id = "lb_current_oxygen", text_color = blue_light_2, text_size = 12 },
                                            { type = "view", id = "img_oxygen", size = {6, 12}, background = 0xffffffff },
                                            { type = "label", id = "lb_oxygen", text_font = "sui", text_color = 0xb4b4b4ff, text_size = 8, size = {300, 25}},
                                        }},

                                    }}
                                }},

                            }},

                        }},
                    }},
                }},
            }},
        }},


    }
})