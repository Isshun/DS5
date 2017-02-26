ui:extend({
    type = "list",
    name = "base.ui.tooltip_parcel",
    controller = "org.smallbox.faraway.client.controller.world.ParcelTooltipController",
    visible = false,
    background = 0x55000000,
    size = {360, 120},
    position = {1030, 720},
    views = {
        { type = "view", id = "content", views = {
            { type = "label", id = "lb_position", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
            { type = "label", id = "lb_ground_info", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
            { type = "label", id = "lb_rock_info", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
        }}
    }
})
