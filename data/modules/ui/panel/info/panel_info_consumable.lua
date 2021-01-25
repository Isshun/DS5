ui:extend({
    type = "list",
    id = "base.ui.info_consumable",
    controller = "org.smallbox.faraway.client.controller.ConsumableInfoController",
    parent = "base.ui.right_panel.sub_controller_full",
    visible = false,
    size = {panel_width, 940},
    views = {

        { type = "view", background = blue_dark_3, views = {
            { type = "view", size = {400 - 6, 940 - 6}, background = 0x181818ff, position = {3, 3}, views = {
                { type = "view", size = {392, 932}, position = {1, 1}, background = 0x262626ff, views = {

                { type = "label", id = "lb_name", text_font = "font3", text = "ground", text_size = 26, text_color = 0xb4b4b4ff, margin = {20, 16} },
                { type = "label", action = "onClose", text = "x", text_font = "whitrabt", outlined = false, text_color = 0x181818ff, background = 0xb4b4b4ff, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {349, 15}},
                { type = "view", size = {360, 1}, position = {15, 59}, background = 0xb4b4b4ff},
                { type = "view", size = {120, 120}, position = {255, 78}, background = 0xb4b4b4ff, views = {
                    { type = "image", id = "image", position = {10, 10}, size = {64, 64}},
                }},

                { type = "list", position = {16, 80}, views = {

                    { type = "list", views = {
                        { type = "label", id = "lb_quantity", text_font = "font3", text_color = 0xb4b4b4ff, text_size = 16},
                        { type = "label", id = "lb_job", text_size = 16},
                        { type = "label", text = "Products", text_size = 22, text_color = 0xb4b4b4ff },
                        { type = "label", id = "lb_product", text_size = 16, text_color = 0xb4b4b4ff },
                    }},

                }},

            }},
            }},
        }},

    }
})