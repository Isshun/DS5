ui:extend({
    type = "list",
    id = "base.ui.tooltip_parcel",
    parent = "base.ui.tooltip.views",
    controller = "org.smallbox.faraway.client.controller.world.ParcelTooltipController",
    visible = false,
    size = {360, 108},
    views = {
        { type = "image", src = "[base]/graphics/tooltip.png", size = {300, 100}, position = {0, -10}},
        { type = "list", id = "content", size = {356, 104}, position = {2, 2}, views = {
            { type = "label", id = "lb_position", text_color = 0xffffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
            { type = "label", id = "lb_ground_info", text_color = 0xffffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
            { type = "label", id = "lb_rock_info", text_color = 0xffffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
        }}
    }
})