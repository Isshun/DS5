ui:extend({
    type = "view",
    id = "base.ui.info.room",
    controller = "org.smallbox.faraway.client.controller.room.RoomInfoController",
    parent = "base.ui.right_panel.sub_controller",
    level = 10,
    visible = false,
    views = {
        { type = "label", text = "Room", text_color = blue_light_2, text_size = 12, position = {12, 8}},
        { type = "view", size = {348, 1}, background = blue_light_2, position = {12, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {12, 37}, size = {100, 40}, text_color = blue_light_5 },
        { type = "list", position = {10, 70}, views = {
            { type = "label", id = "lb_label", text_color = blue_light_5, text_size = 16, position = {0, 24}, padding = 10, size = {100, 40}},
            { type = "label", id = "lb_parcels", text_color = blue_light_5, text_size = 16, position = {0, 24}, padding = 10, size = {100, 40}},
        }},
        { type = "view", id = "base.ui.info.room.content", position = {10, 200}, special = true},
    }
})