ui:extend({
    type = "list",
    name = "base.ui.tooltip_parcel",
    parent = "base.ui.tooltip.views",
    controller = "org.smallbox.faraway.client.controller.world.ParcelTooltipController",
    visible = false,
    size = {360, 108},
    views = {
        { type = "list", id = "content", views = {
            { type = "label", id = "lb_position", text_color = 0xffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
            { type = "label", id = "lb_ground_info", text_color = 0xffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
            { type = "label", id = "lb_rock_info", text_color = 0xffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
        }}
    }
})