ui:extend({
    type = "view",
    id = "base.ui.info.room.quarter",
    controller = "org.smallbox.faraway.client.controller.room.RoomInfoQuarterController",
    parent = "base.ui.info.room.content",
    visible = false,
    views = {
        { type = "label", text = "QUARTER", text_color = color1, text_size = 12, position = {12, 8}},
    }
})