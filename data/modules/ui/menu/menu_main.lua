data:extend({
    type = "view",
    id = "base.ui.menu_main",
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", src = "[base]/graphics/mars_gallery_habitat_3.jpg", size = {1920, 1200}},
        { type = "list", position = {500, 400}, views = {
            { type = "label", text = "Continue", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() application:startLastGame() end},
            { type = "view", background = 0x55ffffff, size = {280, 1}, margin = {10, 0, 0, 10}},
            { type = "label", text = "New game", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_main_menu("base.ui.menu_new_planet") end},
            { type = "label", text = "Load game", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_main_menu("base.ui.menu_load") end},
            { type = "label", text = "Exit", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() application:exit() end},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_game_quit then
            view:setVisible(true)
        end
    end,
})

function open_main_menu(menu_name)
    application.ui:findById("base.ui.menu_main"):setVisible(false)
    application.ui:findById(menu_name):setVisible(true)
end