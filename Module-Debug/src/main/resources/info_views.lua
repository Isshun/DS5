data:extend({
    type = "view",
    name = "base.ui.info_views",
    controller = "org.smallbox.faraway.module.dev.InfoViewsController",
    position = {300, 100},
    size = {300, 600},
    background = 0x55bbdd,
    visible = true,
    views = {
        { type = "label", text = "DEBUG INFO VIEWS", text_size = 14, position = {10, 8}},
        { type = "list", id = "views_list", position = {10, 30}},
    },
})