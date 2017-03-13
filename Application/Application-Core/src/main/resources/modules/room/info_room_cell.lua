ui:extend({
    type = "view",
    id = "base.ui.info.room.cell",
    controller = "org.smallbox.faraway.client.controller.room.RoomInfoCellController",
    parent = "base.ui.info.room.content",
    visible = false,
    views = {
        { type = "label", text = "CELL", text_color = color1, text_size = 12, position = {12, 8}},
    }
})