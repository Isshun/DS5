ui:extend({
    type = "view",
    id = "base.ui.info_views",
    controller = "org.smallbox.faraway.module.dev.InfoViewsController",
    position = {300, 100},
    size = {300, 600},
    visible = false,
    background = 0x55bbdd,
    views = {
        { type = "label", id = "header", text = "DEBUG INFO VIEWS", text_size = 14, position = {10, 8}, size = {300, 30}},
        { type = "list", id = "views_list", position = {10, 30}},
    },
})