ui:extend({
    type = "view",
    id = "base.ui.click_debug_views",
    controller = "org.smallbox.faraway.module.dev.ClickDebugController",
    position = {500, 800},
    size = {600, 200},
    background = 0x228844,
    visible = true,
    views = {
        { type = "label", id = "entry", text_size = 14, position = {10, 8}},
    },
})