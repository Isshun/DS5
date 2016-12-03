ui:extend({
    type = "view",
    name = "base.ui.debug.renders_list",
    controller = "org.smallbox.faraway.module.dev.DebugRendersController",
    position = {600, 100},
    visible = false,
    views = {
        { type = "label", id = "header", text = "DEBUG RENDERS", text_size = 14, position = {10, 8}, size = {300, 30}, background = 0x55bbdd},
        { type = "list", id = "list", position = {10, 30}, size = {300, 600}, background = 0x55bbdd},
    },
})