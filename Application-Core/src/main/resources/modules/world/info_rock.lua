ui:extend({
    type = "list",
    id = "base.ui.info_rock",
    controller = "org.smallbox.faraway.client.controller.RockInfoController",
    parent = "base.ui.right_panel",
    visible = false,
    views = {
        { type = "label", text = "Rock", text_color = color1, text_size = 12, margin = {12, 12, 0, 12}},
        { type = "label", id = "lb_label", text = "name", text_size = 28, text_color = color2, margin = {12, 12} },

        { type = "list", position = {12, 0}, views = {
            { type = "label", id = "lb_name", text_size = 16},
            { type = "label", id = "lb_quantity", text_size = 16},
            { type = "label", id = "lb_job", text_size = 16},
            { type = "label", text = "Products", text_size = 22, text_color = color2 },
            { type = "label", id = "lb_product", text_size = 16, text_color = color1 },
        }},

    }
})