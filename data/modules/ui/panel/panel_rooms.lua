ui:extend({
    type = "view",
    id = "base.ui.right_panel.rooms",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.room.RoomPanelController",
    visible = false,
    views = {
        { type = "label", text = "Rooms", text_color = blue_light_2, text_size = 28, position = {12, 16}},
        { type = "list", id = "list_rooms_add", position = {10, 40}},
        { type = "list", id = "list_rooms_sub", position = {205, 40}},
    },
})