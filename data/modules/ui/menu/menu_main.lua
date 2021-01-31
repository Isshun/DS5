ui:extend({
    type = "view",
    id = "base.ui.menu.main",
    controller = "org.smallbox.faraway.client.menu.controller.MenuMainController",
    in_game = false,
    visible = false,
    styles = {
        { id = "menu_entry", text_font = "font3", text_size = 22, text_color = yellow, padding = 16, size = {280, 48}, margin = {10, 0, 0, 10} }
    },
    views = {
        { visible = false, type = "image", id = "img_bg", src = "[base]/background/17520.jpg", size = {application.screen_width, application.screen_height}},
        { type = "label", text = "VOID", text_color = 0x00000099, text_font = "font3", text_size = 180, position = {application.screen_width / 2 - 220, 160}},
        { type = "list", position = {application.screen_width / 2 - 300 / 2, application.screen_height / 2 - 200}, views = {
            { type = "label", text = "Continue", style = "menu_entry", action = "onActionContinue" },
            { type = "view", background = 0x55ffffff, size = {280, 1}, margin = {10, 0, 0, 10}},
            { type = "label", text = "New game", style = "menu_entry", action = "onActionNewGame" },
            { type = "label", text = "Load game", style = "menu_entry", action = "onActionLoad" },
            { type = "label", text = "Settings", style = "menu_entry", action = "onActionSettings" },
            { type = "label", text = "Exit", style = "menu_entry", action = "onActionExit" },
        }},
    },
})
