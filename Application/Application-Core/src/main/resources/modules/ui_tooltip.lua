ui:extend({
    type = "list",
    name = "base.ui.tooltip",
    controller = "org.smallbox.faraway.client.controller.TooltipController",
    visible = true,
    background = 0x55000000,
    size = {360, 120},
    -- TODO: transformer en position relative
    position = {1030, 700},
    views = {
        { type = "label", id = "lb_name", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
        { type = "view", id = "sub_view", position = {10, 28}, size = {100, 100}},
--        { type = "label", id = "lb_position", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
--        { type = "label", id = "lb_ground_info", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
--        { type = "label", id = "lb_rock_info", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
    }
})
