ui:extend({
    type = "view",
    id = "base.ui.menu.settings",
    controller = "org.smallbox.faraway.client.controller.menu.MenuSettingsController",
    size = {application.screen_width, application.screen_height},
    background = 0x000000ff,
    in_game = false,
    visible = false,
    views = {
        { type = "list", size = {800, 600}, background = 0x25c9cbbb, position = {application.screen_width / 2 - 800 / 2, application.screen_height / 2 - 200}, views = {
            { type = "grid", id = "grid_settings_sections", columns = 4, column_width = 200, row_height = 50, background = color3, size = {800, 50}, views = {
                { type = "label", id = "btGraphic", action = "onOpenGraphic", text = "Graphic", text_size = 28, padding = 16, size = {200, 50}, padding = 14, background = 0x25c9cbff},
                { type = "label", id = "btSound", action = "onOpenSound", text = "Sound", text_size = 28, padding = 16, size = {200, 50}, padding = 14},
                { type = "label", id = "btBindings", action = "onOpenBindings", text = "Bindings", text_size = 28, padding = 16, size = {200, 50}, padding = 14},
                { type = "label", id = "btGameplay", action = "onOpenGameplay", text = "Gameplay", text_size = 28, padding = 16, size = {200, 50}, padding = 14},
            }},
            { type = "list", background = 0x25c9cbff, size = {800, 4}},
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
            { type = "list", id = "sound_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "label", text = "Music", text_size = 16, padding = 10},
                { type = "label", text = "Game sounds", text_size = 16, padding = 10},
                { type = "label", text = "Notifications", text_size = 16, padding = 10},
            }},
            { type = "list", id = "bindings_sub_menu", visible = false, position = {0, 2}, size = {800, 490}, views = {
                { type = "label", text = "UI", text_size = 16, padding = 10},
                { type = "label", text = "Game", text_size = 16, padding = 10},
            }},
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
            { type = "view", size = {800, 45}, views = {
                { type = "label", text = "Close", action = "onClose", text_size = 22, padding = 16, size = {160, 45}, position = {458, 0}, background = {regular = 0x01000000, focus = 0x25c9cbff}},
                { type = "label", text = "Apply", action = "onApply", text_size = 22, padding = 16, size = {160, 45}, position = {630, 0}, background = {regular = 0x01000000, focus = 0x25c9cbff}},
            }}
        }},
    },
})