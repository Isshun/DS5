ui:extend({
    type = "view",
    name = "base.ui.debug_system",
    controller = "org.smallbox.faraway.module.dev.controller.DebugSystemController",
    position = {600, 0},
    visible = true,
    views = {
        { type = "view", id ="gauge_heap", size = {160, 20}, background = 0x125556},
        { type = "view", id ="gauge_heap_content", position = {2, 2}, background = 0x349394},
        { type = "label", id ="lb_heap", text_size = 12, position = {8, 6}},
    },
})