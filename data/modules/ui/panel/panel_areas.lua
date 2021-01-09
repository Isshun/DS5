ui:extend({
    type = "view",
    id = "base.ui.right_panel.areas",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.area.AreaPanelController",
    visible = false,
    views = {
        { type = "label", text = "Areas", text_color = blue_light_2, text_size = 28, position = {12, 16}},
        { type = "list", id = "list_areas_add", position = {10, 40}, template = {
            { type = "label", text_color = blue_light_2, text_size = 18, padding = 10, background = {regular = 0x121c1eff, focus = 0x25c9cbff}, size = {160, 40}, position = {12, 16}},
        }},
        { type = "list", id = "list_areas_sub", position = {205, 40}},
    },
})