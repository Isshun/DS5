ui:extend({
    type = "view",
    id = "base.ui.right_panel.areas",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.area.AreaPanelController",
    visible = false,
    views = {
        { type = "list", id = "list_areas_add", position = {0, 0}, template = {
            { type = "view", size = {400, 65}, views = {
                { type = "label", id = "lb_area", text_align = "CENTER_VERTICAL", text_color = green, text_size = 20, text_font = "font3", size = {160, 65}, position = {18, 0}},
                { type = "view", background = green_50, size = {394, 1}, position = {0, 65}},
                { type = "label", text = "+", text_align = "CENTER", text_color = blue_dark_4, background = green, text_size = 34, text_font = "font3", size = {34, 34}, position = {292, 16}, id = "bt_add"},
                { type = "label", text = "-", text_align = "CENTER", text_color = blue_dark_4, background = green, text_size = 34, text_font = "font3", size = {34, 34}, position = {342, 16}, id = "bt_remove"},
            }},
        }},
        { type = "list", id = "list_areas_sub", position = {205, 40}},
    },
})