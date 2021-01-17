local button_size = 50

ui:extend({
    type = "list",
    id = "base.ui.right_panel.build",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.BuildController",
    position = {0, 0},
    visible = false,
    views = {
        { type = "label", text = "Build 3", text_color = blue_light_2, text_size = 12, margin = {12, 0}},
        { type = "view", views = {

            -- Left panel
            { type = "list", id = "list_categories", position = {0, 0}, template = {
                { type = "view", size = {button_size + 2, button_size}, background = blue_dark_5, views = {
                    { type = "image", id = "img_category", size = {button_size - 2, button_size - 2}, position = {2, 2}, background = blue_dark_1},
                }},
            }},

            -- Right panel
            { type = "view", size = {300, 500}, position = {button_size, 0}, background = blue_dark_5, views = {
                { type = "view", size = {300 - 4, 500 - 4}, position = {2, 2}, background = blue_light_2, views = {
                    {type = "label", id = "lb_category", size = {300 - 4, 26}, text_align = "right", padding = {8, 8}, background = blue_light_1, text_size = 14},
                    {type = "grid", id = "grid_items", position = {4, 31}, columns = 3, column_width = 98, row_height = 98, template = {
                        {type = "label", size = {92, 92}, background = blue_light_3, text = "gg", text_size = 14},
                    }}
                }}
            }},

        }},
        { type = "label", id = "contentLabel", text_size = 22, position = {12, 30}},
    }
})
