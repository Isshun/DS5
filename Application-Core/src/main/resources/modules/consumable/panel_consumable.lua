ui:extend({
    type = "view",
    id = "base.ui.right_panel.consumable",
    controller = "org.smallbox.faraway.client.controller.ConsumableController",
    parent = "base.ui.right_panel.sub_controller",
    level = 10,
    visible = false,
    views = {
        { type = "list", id = "consumable_list", size = {380, 1}, position = {10, 22}},
    },
})