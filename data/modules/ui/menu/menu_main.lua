ui:extend({
    type = "view",
    id = "base.ui.menu.main",
    controller = "org.smallbox.faraway.client.menu.controller.MenuMainController",
    in_game = false,
    visible = false,
    views = {
        { type = "image", src = "[base]/background/17520.jpg", size = {application.screen_width, application.screen_height}},
        { type = "label", text = "Void", text_color = 0xffffff99, text_size = 101, position = {application.screen_width / 2 - 220, 160}},
        { type = "list", position = {application.screen_width / 2 - 300 / 2, application.screen_height / 2 - 200}, views = {
            { type = "label", text = "Continue", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionContinue" },
            { type = "view", background = 0x55ffffff, size = {280, 1}, margin = {10, 0, 0, 10}},
            { type = "label", text = "New game", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionNewGame" },
            { type = "label", text = "Load game", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionLoad" },
            { type = "label", text = "Settings", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionSettings" },
            { type = "label", text = "Exit", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionExit" },
        }},
    },
})
