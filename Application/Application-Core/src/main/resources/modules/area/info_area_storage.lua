ui:extend({
    type = "view",
    id = "base.ui.info.area.storage",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoStorageController",
    parent = "base.ui.info.area.content",
    visible = false,
    views = {
        { type = "label", text = "STORAGE", text_color = color1, text_size = 16, position = {12, 8}},
--        { type = "grid", id = "grid_category", columns = 3, column_width = 100, row_height = 30, position = {12, 30}},
        { type = "list", id = "list_storage", position = {12, 30}},
    }
})