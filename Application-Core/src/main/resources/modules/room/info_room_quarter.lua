ui:extend({
    type = "list",
    id = "base.ui.info.room.quarter",
    controller = "org.smallbox.faraway.client.controller.room.RoomInfoQuarterController",
    parent = "base.ui.info.room.content",
    visible = false,
    views = {
        { type = "label", text = "QUARTER", text_color = color1, text_size = 14},

        { type = "view", size = {100, 30}, views = {
            { type = "label", id = "lb_owner", text_color = color2, text_size = 14},
            { type = "label", id = "bt_define_owner", text = "Owner: define", action = "onDefineOwner", text_color = color2, text_size = 14, size = {100, 30}},
        }},

        { type = "list", id = "list_characters", position = {12, 8}},
    }
})