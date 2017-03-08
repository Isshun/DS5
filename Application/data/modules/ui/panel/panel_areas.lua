ui:extend({
    type = "view",
    id = "base.ui.right_panel.areas",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.AreaController",
    visible = false,
    views = {
        { type = "label", text = "Areas", text_color = 0x679B99, text_size = 28, position = {12, 16}},
        { type = "list", id = "list_areas_add", position = {10, 40}},
        { type = "list", id = "list_areas_sub", position = {205, 40}},
    },
})