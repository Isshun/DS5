ui:extend({
    type = "view",
    name = "base.ui.debug_console",
    controller = "org.smallbox.faraway.module.dev.controller.DebugConsoleController",
    position = {600, 600},
    visible = true,
    views = {
        { type = "label", id ="lb_entry", visible = false, text = "", text_size = 14, position = {10, 8}, size = {300, 40}, background = 0x55bbdd},
    },
})