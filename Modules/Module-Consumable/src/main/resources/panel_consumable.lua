ui:extend({
    type = "view",
    id = "base.ui.info_consumable",
    controller = "org.smallbox.faraway.module.consumable.ConsumableController",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    level = 10,
    visible = false,
    views = {
        { type = "list", id = "consumable_list", size = {380, 1}, position = {10, 22}},
    },
})