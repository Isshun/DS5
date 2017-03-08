ui:extend({
    type = "view",
    id = "base.ui.info.area",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoController",
    parent = "base.ui.right_panel",
    level = 10,
    visible = false,
    views = {
        { type = "label", text = "Area", text_color = 0x679B99, text_size = 12, position = {12, 8}},
        { type = "view", size = {348, 1}, background = 0x679B99, position = {12, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {12, 37}, size = {100, 40}, text_color = 0xB4D4D3 },
        { type = "list", position = {10, 70}, views = {
            { type = "label", id = "lb_label", text_color = 0xB4D4D3, text_size = 16, position = {0, 24}, padding = 10, size = {100, 40}},
            { type = "label", id = "lb_parcels", text_color = 0xB4D4D3, text_size = 16, position = {0, 24}, padding = 10, size = {100, 40}},
        }},
        { type = "view", id = "base.ui.info.area.content", position = {10, 200}, special = true},
    }
})