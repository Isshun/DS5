local panel_width = 400
local button_size = 54
local button_inner_size = 48
local button_padding = (button_size - button_inner_size) / 2
local item_size = 85
local item_inner_size = 48
local item_padding = (item_size - item_inner_size) / 2
local grid_cols = 6

ui:extend({
    type = "list",
    id = "base.ui.right_panel.build",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.BuildController",
    visible = false,
    views = {
        { type = "view", views = {

            -- Top panel
            { type = "grid", id = "list_categories", columns = grid_cols, column_width = button_size + 10, row_height = button_size, position = {8, 8}, template = {
                { type = "view", size = {button_size, button_size}, background = blue_dark_4, views = {
                    { type = "view", id = "bg_category_off", size = {button_size, button_size}, background = 0xffffffdd},
                    { type = "view", id = "bg_category_on", size = {button_size, button_size}, background = blue},
                    { type = "image", id = "img_category", size = {button_inner_size, button_inner_size}, position = {button_padding, button_padding}},
                }},
            }},

            { type = "label", id = "lb_category", position = {12, 81}, text_color = blue_light_3, text_font = "font3", text_size = 26 },

            { type = "view", size = {368, 2}, background = blue_light_3, position = {12, 115}},

            -- Item grid
            {type = "grid", id = "grid_items", position = {12, 134}, columns = 4, column_width = 95, row_height = 95, template = {
                { type = "view", size = {item_size, item_size}, background = 0xffffffaa, views = {
                    {type = "image", id = "img_item", position = {item_padding, item_padding}, size = {item_inner_size, item_inner_size}},
                    {visible = false, type = "label", id = "lb_item", text_align = "BOTTOM_CENTER", text_font = "font3", text_color = blue_dark_4, size = {item_size, item_size}, text = "gg", text_size = 12, position = {0, -8}},
                }},
            }}

        }},
        { type = "label", id = "contentLabel", text_size = 22, position = {12, 30}},
    }
})
