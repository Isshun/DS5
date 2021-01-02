ui:extend({
    type = "list",
    id = "base.ui.info.area.storage",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoStorageController",
    parent = "base.ui.right_panel.sub_controller",
    visible = false,
    views = {
        { type = "view", size = {348, 25}, background = red_light_5, views = {
            { type = "label", id = "lb_parcel", text = "parcel", text_size = 16, text_color = red_dark_3, margin = {7, 5}},
            { type = "label", id = "lb_space", text_color = red_dark_3, text_size = 12, margin = {9, 12, 0, 12}, position = {270, 0}},
        }},

        { type = "view", size = {348, 500}, background = red_light_5, views = {

            { type = "view", size = {1, 40}, position = {0, 0}, background = red_light_5},
            { type = "view", size = {1, 40}, position = {347, 0}, background = red_light_5},

            { type = "list", size = {346, 499}, position = {1, 0}, background = red_dark_1, views = {

                { type = "label", text = "Prority", text_color = red_dark_3, text_size = 12, size = {300, 16}, position = {305, 0}},
                { type = "grid", columns = 5, column_width = 50, row_height = 42, position = {110, 16}, views = {
                    { type = "label", text = "1", id = "btPriority1", text_color = red_dark_3, text_size = 14, padding = {8, 18}, size = {40, 26}, background = red_dark_5 },
                    { type = "label", text = "2", id = "btPriority2", text_color = red_dark_3, text_size = 14, padding = {8, 18}, size = {40, 26}, background = red_dark_5 },
                    { type = "label", text = "3", id = "btPriority3", text_color = red_dark_3, text_size = 14, padding = {8, 18}, size = {40, 26}, background = red_dark_5 },
                    { type = "label", text = "4", id = "btPriority4", text_color = red_dark_3, text_size = 14, padding = {8, 18}, size = {40, 26}, background = red_dark_5 },
                    { type = "label", text = "5", id = "btPriority5", text_color = red_dark_3, text_size = 14, padding = {8, 18}, size = {40, 26}, background = red_dark_5 },
                }},

                { type = "label", text = "Items", text_color = red_dark_3, text_size = 12, position = {0, 55}},
                { type = "list", id = "list_storage", position = {0, 71}},

            }},
        }},

--        { type = "label", text = "Space", text_color = blue_light_2, text_size = 12, size = {35, 24}},
--        { type = "label", id = "lb_space", text_color = blue_light_5, text_size = 16, text_align = "center", position = {0, 16}, size = {35, 24}},

    }
})