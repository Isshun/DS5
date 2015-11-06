data:extend({
    type = "list",
    id = "base.ui.menu_pause",
    position = {500, 400},
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "label", text = "Resume", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}},
        { type = "label", text = "Save", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_save") end},
        { type = "label", text = "Load", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_load") end},
        { type = "label", text = "Settings", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_settings") end},
        { type = "label", text = "Quit", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 10, 10}, on_click = function()
            open_menu("base.ui.menu_main")
            application:stopGame()
        end},
    },

    on_event = function(view, event, data)
        if event == application.events.on_game_paused then
            print("open")
            view:setVisible(true)
        end
        if event == application.events.on_game_resume then
            print("bye")
            view:setVisible(false)
        end
    end,
})

function open_menu(menu_name)
    application.ui:findById("base.ui.menu_pause"):setVisible(false)
    application.ui:findById(menu_name):setVisible(true)
end