local window_width = 400
local window_pos = 310
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
    size = {panel_width, application.screen_height - window_pos - 14},
    views = {

        { type = "grid", columns = 4, column_width = 98, row_height = 40, views = {
            { type = "label", text = "_Dig", background = blue_dark_3, text_color = 0xffffffcc, text_font = "font3", text_align = "center", size = {90, 30} },
            { type = "label", text = "_Dig V", background = blue_dark_3, text_color = 0xffffffcc, text_font = "font3", text_align = "center", size = {90, 30} },
            { type = "label", text = "_Harvest", background = blue_dark_3, text_color = 0xffffffcc, text_font = "font3", text_align = "center", size = {90, 30} },
            { type = "label", text = "_Cancel", background = blue_dark_3, text_color = 0xffffffcc, text_font = "font3", text_align = "center", size = {90, 30} },
        }},

        { type = "view", position = {0, 42}, size = {panel_width, application.screen_height - window_pos - 54}, background = blue_dark_4, views = {
            { type = "view", id = "base.ui.right_panel.sub_controller_full", special = true},
        }},

    },
})
