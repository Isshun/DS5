ui:extend({
    type = "view",
    id = "base.ui.menu.new_crew",
    controller = "org.smallbox.faraway.client.menu.controller.MenuCrewController",
    size = {300, -1},
    in_game = false,
    visible = false,
    views = {
        { type = "image", src = "[base]/background/17520.jpg", size = {application.screen_width, application.screen_height}},
        { type = "view", position = {application.screen_width / 2 - 300 / 2, application.screen_height / 2 - 200}, views = {
            { type = "label", text = "Crew", text_size = 38},
            { type = "list", id = "list_crew", position = {0, 40}},
            { type = "label", id = "bt_back", padding = 10, background = {regular = 0xffffff55, focus = 0x14dcb988}, text = "back", position = {0, 350}, text_size = 22, size = {100, 40},
                on_click = function()
                    ui:find("base.ui.menu_new_crew"):setVisible(false)
                    ui:find("base.ui.menu_new_planet_region"):setVisible(true)
                end},
            { type = "label", id = "bt_next", padding = 10, background = {regular = 0xffffff55, focus = 0x14dcb988}, text = "next", text_size = 22, size = {100, 40}, position = {200, 350},
                on_click = function()
                    ui:find("base.ui.menu_new_crew"):setVisible(false)
                    application:sendEvent("new_game.start")
                end},
        }}
    },

})