ui:extend({
    type = "view",
    id = "base.ui.right_panel.consumable",
    controller = "org.smallbox.faraway.client.controller.ConsumableController",
    parent = "base.ui.right_panel.sub_controller",
    level = 10,
    visible = false,
    views = {
        { type = "list", id = "consumable_list", size = {380, 1}, position = {10, 22}, template = {
            { type = "view", size = {100, 30}, views = {
                { type = "label", id = "lb_consumable", text_color = "color2", text_size = 14, padding = 12, position = {25, 0} },
                { type = "image", id = "img_consumable" },
            }},
        }},
    },
})