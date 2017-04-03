ui:extend({
    type = "view",
    id = "base.ui.info.area.storage",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoStorageController",
    parent = "base.ui.info.area.content",
    visible = false,
    views = {
        { type = "label", text = "Space", text_color = color1, text_size = 12, size = {35, 24}},
        { type = "label", id = "lb_space", text_color = color2, text_size = 16, text_align = "center", position = {0, 16}, size = {35, 24}},

        { type = "label", text = "Prority", text_color = color1, text_size = 12, size = {300, 16}, position = {305, 0}},
        { type = "grid", columns = 5, column_width = 50, row_height = 42, position = {110, 16}, views = {
            { type = "label", text = "1", id = "btPriority1", text_color = color1, text_size = 14, padding = {8, 18}, size = {40, 26}, background = 0xff223355 },
            { type = "label", text = "2", id = "btPriority2", text_color = color1, text_size = 14, padding = {8, 18}, size = {40, 26}, background = 0xff223b55 },
            { type = "label", text = "3", id = "btPriority3", text_color = color1, text_size = 14, padding = {8, 18}, size = {40, 26}, background = 0xff224455 },
            { type = "label", text = "4", id = "btPriority4", text_color = color1, text_size = 14, padding = {8, 18}, size = {40, 26}, background = 0xff224b55 },
            { type = "label", text = "5", id = "btPriority5", text_color = color1, text_size = 14, padding = {8, 18}, size = {40, 26}, background = 0xff225555 },
        }},

        { type = "label", text = "Items", text_color = color1, text_size = 12, position = {0, 55}},
        { type = "list", id = "list_storage", position = {0, 71}},
    }
})