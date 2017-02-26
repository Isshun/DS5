ui:extend({
    type = "list",
    name = "base.ui.tooltip_character",
    controller = "org.smallbox.faraway.client.controller.character.CharacterTooltipController",
    visible = false,
    background = 0x000000,
    size = {360, 120},
    -- TODO: transformer en position relative
    position = {1030, 720},
    views = {
        { type = "view", id = "content", views = {
            { type = "label", id = "lb_name", text_color = 0xffffff, text_size = 12, position = {10, 8}, size = {100, 20}},
        }}
    }
})
