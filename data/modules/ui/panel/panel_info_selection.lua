ui:extend({
    type = "view",
    id = "base.ui.info_rock",
    parent = "base.ui.right_panel.sub_controller_full",
    controller = "org.smallbox.faraway.client.controller.SelectionInfoController",
    visible = false,
    size = {400, 940},
    views = {
        { type = "view", background = 0x181818ff, views = {
            { type = "view", size = {392, 932}, position = {4, 4}, background = 0x262626ff, views = {
                { type = "label", visible = false, id = "lb_parcel", position = {200, 0}, text = "parcel", text_size = 16, text_color = 0x357f7fff, margin = {7, 5}},
                { type = "label", id = "lb_position", position = {200, 0}, text_color = 0x357f7fff, text_size = 12, margin = {9, 12, 0, 12}, position = {270, 0}},

                { type = "view", views = {

                    { type = "list", position = {1, 0}, views = {

                        { type = "view", size = {fill, 70}, views = {
    --                        { type = "label", text = "Ground", text_font = "font3", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                            { type = "label", id = "lb_ground", text_font = "font3", text = "ground", text_size = 26, text_color = 0xb4b4b4ff, margin = {20, 16} },
                            { type = "label", action = "onClose", text = "x", text_font = "whitrabt", outlined = false, text_color = 0x181818ff, background = 0xb4b4b4ff, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {349, 15}},
                            { type = "view", size = {360, 1}, position = {15, 59}, background = 0xb4b4b4ff},
                            { type = "view", size = {120, 120}, position = {255, 78}, background = 0xb4b4b4ff},

                            { type = "list", position = {15, 80}, views = {
                                { type = "label", text = "Durability      100 / 100", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, id = "lb_name", text_size = 18},
                                { type = "label", text = "Type                      rock", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, id = "lb_quantity", text_size = 18},
                                { type = "label", text = "Temperature             12", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, id = "lb_quantity", text_size = 18},
                                { type = "label", text = "Type                      rock", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, id = "lb_quantity", text_size = 18},
                                { type = "label", visible = false, text = "Text", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, id = "lb_job", text_size = 16},
                                { type = "label", visible = false, text = "Text", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, text = "Products", text_size = 22, text_color = blue_light_5 },
                                { type = "label", visible = false, text = "Text", size = {300, 32}, text_font = "sui", text_color = 0xb4b4b4ff, id = "lb_product", text_size = 16, text_color = blue_light_2 },
                            }},

                        }},

                        { type = "view", size = {fill, 40}, position = {4, 400}, views = {
                            { type = "label", text = "Rock", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                            { type = "label", id = "lb_rock", text = "rock", text_size = 20, text_color = blue_light_5, margin = {12, 12} },
                        }},

                        { type = "view", size = {fill, 40}, position = {4, 400}, views = {
                            { type = "label", text = "Item", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                            { type = "label", id = "lb_item", text = "item", text_size = 20, text_color = blue_light_5, margin = {12, 12} },
                        }},

                        { type = "view", size = {fill, 40}, position = {4, 400}, views = {
                            { type = "label", text = "Consumable", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                            { type = "label", id = "lb_consumable", text = "consumable", text_size = 20, text_color = blue_light_5, margin = {12, 12} },
                        }},

                    }},
                }},
            }},
        }},
    }
})