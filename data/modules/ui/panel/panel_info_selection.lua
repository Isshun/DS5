ui:extend({
    type = "list",
    id = "base.ui.info_rock",
    controller = "org.smallbox.faraway.client.controller.SelectionInfoController",
    parent = "base.ui.right_panel.sub_controller",
    visible = false,
    views = {
        { type = "view", size = {348, 25}, background = blue_light_5, views = {
            { type = "label", id = "lb_parcel", text = "parcel", text_size = 16, text_color = 0x357f7fff, margin = {7, 5}},
            { type = "label", id = "lb_position", text_color = 0x357f7fff, text_size = 12, margin = {9, 12, 0, 12}, position = {270, 0}},
        }},

        { type = "view", size = {348, 500}, background = blue_light_5, views = {

            { type = "view", size = {1, 40}, position = {0, 0}, background = blue_light_5},
            { type = "view", size = {1, 40}, position = {347, 0}, background = blue_light_5},

            { type = "list", size = {346, 499}, position = {1, 0}, background = 0x144143ff, views = {

                { type = "label", text = "Ground", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                { type = "label", id = "lb_ground", text = "ground", text_size = 20, text_color = blue_light_5, margin = {12, 12} },

                { type = "label", text = "Rock", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                { type = "label", id = "lb_rock", text = "rock", text_size = 20, text_color = blue_light_5, margin = {12, 12} },

                { type = "label", text = "Item", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                { type = "label", id = "lb_item", text = "item", text_size = 20, text_color = blue_light_5, margin = {12, 12} },

                { type = "label", text = "Consumable", text_color = blue_light_2, text_size = 14, margin = {12, 12, 0, 12}},
                { type = "label", id = "lb_consumable", text = "consumable", text_size = 20, text_color = blue_light_5, margin = {12, 12} },

                { type = "list", position = {12, 0}, views = {
                    { type = "label", id = "lb_name", text_size = 16},
                    { type = "label", id = "lb_quantity", text_size = 16},
                    { type = "label", id = "lb_job", text_size = 16},
                    { type = "label", text = "Products", text_size = 22, text_color = blue_light_5 },
                    { type = "label", id = "lb_product", text_size = 16, text_color = blue_light_2 },
                }},

            }},
        }},

    }
})