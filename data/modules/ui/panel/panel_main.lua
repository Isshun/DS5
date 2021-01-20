local window_width = 400
local window_pos = 346
local button_number = 4
local button_width = window_width / 4
local button_height = 52

ui:extend({
    type = "view",
    id = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.MainPanelController",
    align = {"top", "right"},
    position = {10, window_pos},
    background = blue_light_1,
    size = {window_width, application.screen_height - window_pos - 14},
    views = {

        { type = "view", size = {window_width, application.screen_height - window_pos - 14}, background = blue_dark_4, views = {

            { type = "view", position = {0, 0}, id = "content_list", views = {

                -- CONTENT
                { type = "view", id = "view_border", size = {window_width, application.screen_height - window_pos - button_height - 10}, position = {0, button_height - 4}, background = yellow, views = {
                    { type = "view", size = {window_width - 8, application.screen_height - window_pos - button_height - 10 - 8}, position = {4, 4}, background = blue_dark_4, views = {
                        { type = "view", id = "base.ui.right_panel.sub_controller", size = {fill, fill}, special = true},
                    }},
                }},
                -- CONTENT: END

                -- BUTTONS
                { type = "grid", columns = button_number, column_width = button_width, row_height = button_height, size = {window_width, button_height}, views = {
                    { type = "view", id = "bt_crew", size = {button_width, fill}, views = {
                        { type = "view", size = {fill, 4}, background = yellow},
                        { type = "view", id = "focus_crew", size = {button_width, button_height}, background = yellow},
                        { type = "label", id = "lb_crew", text = "CREW", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_crew", size = {button_width, button_height - 4}, background = 0x00000088},
                    }},
                    { type = "view", id = "bt_build", size = {button_width, fill}, views = {
                        { type = "view", size = {fill, 4}, background = blue},
                        { type = "view", id = "focus_build", size = {button_width, button_height}, background = blue},
                        { type = "label", id = "lb_build", text = "BUILD", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_build", size = {button_width, button_height - 4}, background = 0x00000088},
                    }},
                    { type = "view", id = "bt_area", size = {button_width, fill}, views = {
                        { type = "view", size = {fill, 4}, background = green},
                        { type = "view", id = "focus_area", size = {button_width, button_height}, background = green},
                        { type = "label", id = "lb_area", text = "AREA", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_area", size = {button_width, button_height - 4}, background = 0x00000088},
                    }},
                    { type = "view", id = "bt_jobs", size = {button_width, fill}, views = {
                        { type = "view", size = {fill, 4}, background = red},
                        { type = "view", id = "focus_jobs", size = {button_width, button_height}, background = red},
                        { type = "label", id = "lb_jobs", text = "JOBS", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_jobs", size = {button_width, button_height - 4}, background = 0x00000088},
                    }},
                }},
                -- BUTTONS: END

            }},

        }}

    },

})
