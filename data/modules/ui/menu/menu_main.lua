ui:extend({
    type = "view",
    id = "base.ui.menu.main",
    controller = "org.smallbox.faraway.client.menu.controller.MenuMainController",
    in_game = false,
    visible = false,
    styles = {
        { id = "menu_entry", text_font = "font3", text_size = 22, text_align = "CENTER", text_color = blue_dark_5, background = 0xffffff55, focus = 0xffffff88, size = {280, 52}}
    },
    views = {
        { visible = false, type = "image", id = "img_bg", src = "[base]/background/17520.jpg", size = {application.screen_width, application.screen_height}},
        { type = "label", text = "VOID", text_color = 0x00000099, text_font = "font3", text_size = 180, text_align = "CENTER", size = {application.screen_width, 450}},
        { type = "list", position = {application.screen_width / 2 - 300 / 2, application.screen_height / 2 - 200}, spacing = 15, views = {
            { type = "label", text = "Continue", style = "menu_entry", action = "onActionContinue" },
            { type = "view", background = blue_dark_5, size = {280, 2}},
            { type = "label", text = "New game", style = "menu_entry", action = "onActionNewGame" },
            { type = "label", text = "Load game", style = "menu_entry", action = "onActionLoad" },
            { type = "label", text = "Settings", style = "menu_entry", action = "onActionSettings" },
            { type = "label", text = "Exit", style = "menu_entry", action = "onActionExit" },
        }},
    },
})
