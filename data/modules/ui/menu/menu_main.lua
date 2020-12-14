ui:extend({
    type = "view",
    id = "base.ui.menu.main",
    background = 0xdd121c1e,
    controller = "org.smallbox.faraway.client.controller.menu.MenuMainController",
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", src = "[base]/graphics/mars_gallery_habitat_3.jpg", size = {1920, 1200}},
        { type = "list", position = {application.info.screen_width / 2 - 300 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "label", text = "Continue", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionContinue" },
            { type = "view", background = 0x55ffffff, size = {280, 1}, margin = {10, 0, 0, 10}},
            { type = "label", text = "New game", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionNewGame" },
            { type = "label", text = "Load game", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionLoad" },
            { type = "label", text = "Settings", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionSettings" },
            { type = "label", text = "Exit", text_size = 22, padding = 16, background = {regular = 0xffffff55, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, action = "onActionExit" },
        }},
    },
})
