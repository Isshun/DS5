ui:extend({
    type = "view",
    id = "base.ui.game_menu.pause",
    size = {application.screen_width, application.screen_height},
    controller = "org.smallbox.faraway.client.controller.gameMenu.GameMenuPauseController",
    visible = false,
    level = 100,
    views = {

        -- Pause frame
        { type = "view", id = "view_pause", background = 0x00000055, size = {application.screen_width, application.screen_height}, views = {
            { type = "list", background = 0x00ff00ff, border = blue_light_2, position = {application.screen_width / 2 - 150, 200}, size = {250, 350}, views = {
                { type = "label", size = {240, 50}, background = 0x349394ff, margin = {5, 0, 0, 5}, text_size = 20, text_align = "center", text = "Resume", action="onActionResume"},
                { type = "label", size = {240, 50}, background = 0x349394ff, margin = {5, 0, 0, 5}, text_size = 20, text_align = "center", text = "Save", action="onActionSave"},
                { type = "label", size = {240, 50}, background = 0x349394ff, margin = {5, 0, 0, 5}, text_size = 20, text_align = "center", text = "Load", action="onActionLoad"},
                { type = "label", size = {240, 50}, background = 0x349394ff, margin = {5, 0, 0, 5}, text_size = 20, text_align = "center", text = "Exit", action="onActionExit"},
            }}
        }},
--
--         { type = "label", text = "Stand-by", text_size = 42, text_color = 0x25c9cbff, position = {20, application.screen_height - 52}},
--         { type = "view", id = "bt_cursor", background = 0x25c9cbff, size = {20, 32}, position = {210, application.screen_height - 52}},
--         { type = "list", background = 0xffff00ff, position = {application.screen_width / 2 - 280 / 2, application.screen_height / 2 - 240 / 2}, views = {
--             { type = "label", style = "menu.pause.button", action = "onResume", text = "Resume"},
--             { type = "label", style = "menu.pause.button", action = "onSave", text = "Save"},
--             { type = "label", style = "menu.pause.button", action = "onLoad", text = "Load"},
--             { type = "label", style = "menu.pause.button", action = "onSettings", text = "Settings"},
--             { type = "label", style = "menu.pause.button", action = "onQuit", text = "Quit"},
--         }},
    },
})