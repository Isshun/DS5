local window_width = 400
local window_pos = 346
local button_number = 4
local button_width = window_width / 4
local button_height = 52

ui:extend({
    type = "view",
    debug = true,
    id = "base.ui.right_panel.container",
    controller = "org.smallbox.faraway.client.controller.MainPanelContainerController",
    align = {"top", "right"},
    position = {10, window_pos},
    size = {window_width, application.screen_height - window_pos - 14},
    views = {
        { type = "view", size = {window_width, application.screen_height - window_pos - 14}, background = blue_dark_4, views = {
            { type = "view", id = "base.ui.right_panel.sub_controller_full", special = true},
        }},
    },
})
