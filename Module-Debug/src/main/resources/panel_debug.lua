ui:extend({
    type = "list",
    id = "base.ui.info_character",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    controller = "org.smallbox.faraway.DebugController",
    visible = false,
    views = {
        { type = "checkbox", text = "debug view", text_size = 14, position = {10, 8}, size = {200, 20}, id = "cbDebugView"},
    },
})