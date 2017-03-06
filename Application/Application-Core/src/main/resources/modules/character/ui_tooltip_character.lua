ui:extend({
    type = "list",
    name = "base.ui.tooltip_character",
    parent = "base.ui.tooltip.views",
    controller = "org.smallbox.faraway.client.controller.character.CharacterTooltipController",
    visible = false,
    size = {360, 108},
    views = {
        { type = "view", id = "content", views = {
            { type = "label", id = "lb_name", text_color = 0xffffff, text_size = 14, position = {10, 8}, size = {100, 20}},
        }}
    }
})