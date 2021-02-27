local window_width = 400
local window_pos = 346
local button_number = 4
local button_width = window_width / 4
local button_height = 52

ui:extend({
    type = "view",
    debug = true,
    id = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.MainPanelController",
    parent = "base.ui.right_panel.sub_controller_full",
    views = {

        { type = "view", views = {

            { type = "view", position = {0, 0}, id = "content_list", views = {

                -- BUTTONS
                { type = "grid", columns = button_number, column_width = panel_width / 4, row_height = button_height, size = {panel_width, button_height}, views = {
                    { type = "view", id = "bt_crew", action = "openPaneCrew", size = {panel_width / 4, button_height}, views = {
                        { type = "view", size = {fill, 4}, background = yellow},
                        { type = "view", id = "focus_crew", background = yellow},
                        { type = "label", id = "lb_crew", text = "CREW", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_crew", size = {panel_width / 4, button_height - 4}, background = 0x00000088},
                    }},
                    { type = "view", id = "bt_build", action = "openPaneBuild", size = {panel_width / 4, button_height}, views = {
                        { type = "view", size = {fill, 4}, background = blue},
                        { type = "view", id = "focus_build", background = blue},
                        { type = "label", id = "lb_build", text = "BUILD", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_build", size = {panel_width / 4, button_height - 4}, background = 0x00000088},
                    }},
                    { type = "view", id = "bt_areas", action = "openPaneArea", size = {panel_width / 4, button_height}, views = {
                        { type = "view", size = {fill, 4}, background = green},
                        { type = "view", id = "focus_area", background = green},
                        { type = "label", id = "lb_area", text = "RES.", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_area", size = {panel_width / 4, button_height - 4}, background = 0x00000088},
                    }},
                    { type = "view", id = "bt_jobs", action = "openPaneJobs", size = {panel_width / 4, button_height}, views = {
                        { type = "view", size = {fill, 4}, background = red},
                        { type = "view", id = "focus_jobs", background = red},
                        { type = "label", id = "lb_jobs", text = "JOBS", text_size = 22, text_font = "font3", text_color = blue_dark_4, text_align = "center", size = {fill, button_height - 8}, position = {0, 4}},
                        { type = "view", id = "mask_jobs", size = {panel_width / 4, button_height - 4}, background = 0x00000088},
                    }},
                }},
                -- BUTTONS: END

                -- CONTENT
                { type = "view", size = {panel_width, application.screen_height - window_pos - button_height - 10}, position = {0, button_height - 4}, views = {
                    { type = "view", id = "base.ui.right_panel.sub_controller", size = {fill, fill}, special = true},
                }},
                -- CONTENT: END

            }},

        }},

    },

})
