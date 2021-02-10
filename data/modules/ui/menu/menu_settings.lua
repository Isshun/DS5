local window_width = 1200
local window_height = 900

ui:extend({
    type = "view",
    id = "base.ui.menu.settings",
    controller = "org.smallbox.faraway.client.menu.controller.MenuSettingsController",
    in_game = false,
    visible = false,
    size = {window_width, window_height},
    position = {application.screen_width / 2 - window_width / 2, application.screen_height / 2 - window_height / 2},
    styles = {
        {
            id = "header_button",
            text_font = "font3",
            text_color = 0x00000088,
            text_size = 34,
            text_focus_color = 0xffffffff,
            text_align = "CENTER",
            size = {300, 65},
        }
    },
    views = {
        { type = "list", background = 0xffffff88, border = 0x000000cc, border_size = 4, views = {

            -- Buttons header
            { type = "grid", id = "grid_settings_sections", columns = 4, column_width = (window_width - 8) / 4, row_height = 65, background = 0x00000055, position = {4, 4}, size = {window_width - 8, 50}, views = {
                { type = "label", id = "btGraphic", action = "onOpenGraphic", style = "header_button", text = "Graphic" },
                { type = "label", id = "btSound", action = "onOpenSound", style = "header_button", text = "Sound" },
                { type = "label", id = "btBindings", action = "onOpenBindings", style = "header_button", text = "Bindings" },
                { type = "label", id = "btGameplay", action = "onOpenGameplay", style = "header_button", text = "Gameplay" },
            }},

--            { type = "list", background = 0x25c9cbff, size = {800, 4}},

            -- Graphic
            { type = "list", id = "graphic_sub_menu", visible = true, position = {0, 2}, size = {800, 490}, views = {
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Resolution", text_size = 20, padding = 10},
                    { type = "label", id = "bt_ratio", size = {100, 22}, text_size = 16, padding = 10, position = {300, 0}, on_click = function(v)
                        set_ratio(application.config:getNextRatio(settings.ratio))
                    end},
                    { type = "dropdown", id = "dd_resolutions", size = {100, 28}, position = {360, 5} },
                }},
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Screen mode", text_size = 20, padding = 10},
                    { type = "grid", columns = 3, column_width = 140, row_height = 32, position = {300, 0}, views = {
                        { type = "label", id = "bt_screen_borderless", action = "setScreenBorderless", text = "[ ] Borderless", text_size = 16, padding = 10, size = {149, 32}},
                        { type = "label", id = "bt_screen_fullscreen", action = "setScreenFullscreen", text = "[ ] Fullscreen", text_size = 16, padding = 10, size = {149, 32}},
                        { type = "label", id = "bt_screen_window", action = "setScreenWindow", text = "[ ] Window", text_size = 16, padding = 10, size = {149, 32}},
                    }},
                }},
                { type = "label", text = "UI Scale", text_size = 16, padding = 10},
            }},

            -- Sound
            { type = "list", id = "sound_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "view", size = {window_width, 60}, position = {20, 0}, views = {
                    { type = "label", text = "Music", text_font = "sui", text_color = 0x000000cc, text_size = 24, size = {400, 60}, text_align = "LEFT", padding = 10},
                    { type = "slider", id = "slider_music", size = {400, 6}, handle_size = {50, 20}, position = {650, 40}, handle_background = 0x000000ee, background = 0x00000088, views = {
                        { type = "label", text_size = 16, text_color = 0x000000cc, text_size = 24, text_font = "sui", position = {450, -5}},
                    }},
                }},
                { type = "view", size = {window_width, 60}, position = {20, 0}, views = {
                    { type = "label", text = "Sounds", text_font = "sui", text_color = 0x000000cc, text_size = 24, size = {400, 60}, text_align = "LEFT", padding = 10},
                    { type = "slider", size = {400, 6}, handle_size = {50, 20}, position = {650, 40}, handle_background = 0x000000ee, background = 0x00000088, views = {
                        { type = "label", text_size = 16, text_color = 0x000000cc, text_size = 24, text_font = "sui", position = {450, -5}},
                    }},
                }},
            }},

            -- Bindingss
            { type = "list", id = "bindings_sub_menu", visible = false, position = {0, 12}, size = {800, 490}, template = {
                { type = "label", id = "lb_binding", text_color = 0x000000cc, text_focus_color = yellow, text_font = "sui", text_size = 22, size = {800, 32}, text_align = "LEFT", position = {14, 0}}
            }},

            -- Gameplay
            { type = "list", id = "gameplay_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "view", size = {100, 26}, views = {
                    { type = "label", text = "Restraint mouse to window", text_size = 20, padding = 10},
                    { type = "grid", columns = 3, column_width = 140, row_height = 32, position = {300, 0}, views = {
                        { type = "label", id = "lb_screen_mode_2", text = "[ ]", text_size = 16, padding = 10, size = {149, 32},
                        }},
                    }},
                    { type = "view", size = {100, 26}, views = {
                        { type = "label", text = "Scrolling edges", text_size = 20, padding = 10},
                        { type = "grid", columns = 3, column_width = 140, row_height = 32, position = {300, 0}, views = {
                            { type = "label", id = "lb_screen_mode_2", text = "[ ]", text_size = 16, padding = 10, size = {149, 32},
                            }},
                        }},
                    }},
            }},

        }},

        -- Buttons footer
        { type = "view", size = {800, 45}, views = {
            { type = "label", text = "Close", style = "action_button", action = "onClose", text_align = "LEFT", position = {30, window_height - 75} },
            { type = "label", text = "Apply", style = "action_button", action = "onApply", text_align = "RIGHT", position = {window_width - 270, window_height - 75} },
        } },

        { type = "view", background = 0x00000088, id = "frame_new_binding", visible = false},

    },

})