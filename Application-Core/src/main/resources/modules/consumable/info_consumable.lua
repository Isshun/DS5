local window_width = 348
local window_height = 200
local header_height = 32

ui:extend({
    type = "list",
    id = "base.ui.info_consumable",
    controller = "org.smallbox.faraway.client.controller.ConsumableInfoController",
    parent = "base.ui.right_panel.sub_controller",
    visible = false,
    views = {

        { type = "view", size = {window_width, header_height}, background = blue_light_5, views = {
            { type = "label", id = "lb_name", position = {28, 0}, text = "consumable", text_size = 22, text_color = blue_dark_1, margin = {10, 7}},
            { type = "image", id = "image", position = {4, 4}},
        }},

        { type = "view", size = {window_width, window_height}, background = blue_light_5, views = {

            { type = "view", size = {1, 40}, position = {0, 0}, background = blue_light_5},
            { type = "view", size = {1, 40}, position = {347, 0}, background = blue_light_5},

            { type = "list", size = {window_width - 2, window_height - 1}, position = {1, 0}, background = blue_dark_3, views = {

                { type = "list", position = {12, 0}, views = {
                    { type = "label", id = "lb_quantity", text_size = 16},
                    { type = "label", id = "lb_job", text_size = 16},
                    { type = "label", text = "Products", text_size = 22, text_color = blue_light_5 },
                    { type = "label", id = "lb_product", text_size = 16, text_color = blue_light_2 },
                }},

            }},
        }},
    }
})