ui:extend({
    type = "list",
    id = "base.ui.info_character",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    controller = "org.smallbox.faraway.DebugController",
    visible = false,
    views = {
        { type = "checkbox", text = "Display views id", text_size = 14, position = {10, 8}, size = {200, 15}, id = "cbViewId"},
        { type = "checkbox", text = "View debug window", text_size = 14, position = {10, 8}, size = {200, 15}, id = "cbViewWindow"},
        { type = "checkbox", text = "Render debug window", text_size = 14, position = {10, 8}, size = {200, 15}, id = "cbRenderWindow"},
    },
})