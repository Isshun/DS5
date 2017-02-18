ui:extend({
    type = "view",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    id = "base.ui.panel_debug",
    controller = "org.smallbox.faraway.client.controller.DebugController",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("base.ui.panel_debug"):setVisible(false)
        end},
        { type = "label", text = "Debug", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", id = "list_debug", position = {12, 52}},
    },
})