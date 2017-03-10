ui:extend({
    type = "view",
    id = "base.ui.info.area.storage",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoStorageController",
    parent = "base.ui.info.area.content",
    visible = false,
    views = {
        { type = "label", text = "STORAGE", text_color = color1, text_size = 12, position = {12, 8}},
    }
})