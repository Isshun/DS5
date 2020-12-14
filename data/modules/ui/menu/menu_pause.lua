ui:extend({
    type = "view",
    id = "base.ui.menu.pause",
    controller = "org.smallbox.faraway.client.controller.GameMenuController",
    size = {application.screen_width, application.screen_height},
    background = 0x000000ff,
    in_game = false,
    visible = false,
    views = {
        { type = "label", text = "Stand-by", text_size = 42, text_color = 0x25c9cbff, position = {20, application.screen_height - 52}},
        { type = "view", id = "bt_cursor", background = 0x25c9cbff, size = {20, 32}, position = {210, application.screen_height - 52}},
        { type = "list", position = {application.screen_width / 2 - 280 / 2, application.screen_height / 2 - 240 / 2}, views = {
            { type = "label", style = "menu.pause.button", action = "onResume", text = "Resume"},
            { type = "label", style = "menu.pause.button", action = "onSave", text = "Save"},
            { type = "label", style = "menu.pause.button", action = "onLoad", text = "Load"},
            { type = "label", style = "menu.pause.button", action = "onSettings", text = "Settings"},
            { type = "label", style = "menu.pause.button", action = "onQuit", text = "Quit"},
        }},
    },
})