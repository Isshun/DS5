data:extend({
    type = "view",
    id = "base.ui.menu_main",
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", src = "[base]/graphics/mars_gallery_habitat_3.jpg", size = {1920, 1200}},
        { type = "list", position = {application.info.screen_width / 2 - 300 / 2, application.info.screen_height / 2 - 200}, views = {
            { type = "label", text = "Continue", text_size = 22, padding = 16, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function()
                application:sendEvent("load_game.last_game")
            end},
            { type = "view", background = 0x55ffffff, size = {280, 1}, margin = {10, 0, 0, 10}},
            { type = "label", text = "New game", text_size = 22, padding = 16, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_main_menu("base.ui.menu_new_planet") end},
            { type = "label", text = "Load game", text_size = 22, padding = 16, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_main_menu("base.ui.menu_load") end},
            { type = "label", text = "Settings", text_size = 22, padding = 16, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_main_menu("base.ui.menu_settings") end},
            { type = "label", text = "Exit", text_size = 22, padding = 16, background = {regular = 0x55ffffff, focus = 0x8814dcb9}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() application:exit() end},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_game_quit then
            view:setVisible(true)
        end
    end,
})

function open_main_menu(menu_name)
    ui:find("base.ui.menu_main"):setVisible(false)
    ui:find(menu_name):setVisible(true)
end