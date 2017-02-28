ui:extend({
    type = "list",
    name = "base.ui.tooltip",
    controller = "org.smallbox.faraway.client.controller.TooltipController",
    visible = true,
    background = 0x0088dd,
    size = {360, 120},
    align = {"bottom", "right"},
    position = {10, 130},
    views = {
        { type = "label", id = "lb_name", text_color = 0xffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
        { type = "view", id = "base.ui.tooltip.views", special = true, position = {0, 12}, size = {100, 100}},
--        { type = "label", id = "lb_position", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
--        { type = "label", id = "lb_ground_info", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
--        { type = "label", id = "lb_rock_info", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
    }
})
