data:extend({
    type = "view",
    id = "base.ui.menu_pause",
    size = {2000, 2000},
    background = 0xbb000000,
    in_game = false,
    visible = false,
    views = {
        { type = "list", position = {500, 400}, views = {
            { type = "label", text = "Resume", text_size = 22, text_color = 0x38534e, padding = 16, background = 0xbbdddddd, size = {280, 48}, margin = {10, 0, 0, 10}},
            { type = "label", text = "Save", text_size = 22, text_color = 0x38534e, padding = 16, background = 0xbbdddddd, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_save") end},
            { type = "label", text = "Load", text_size = 22, text_color = 0x38534e, padding = 16, background = 0xbbdddddd, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_load") end},
            { type = "label", text = "Settings", text_size = 22, text_color = 0x38534e, padding = 16, background = 0xbbdddddd, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_settings") end},
            { type = "label", text = "Quit", text_size = 22, text_color = 0x38534e, padding = 16, background = 0xbbdddddd, size = {280, 48}, margin = {10, 0, 10, 10}, on_click = function()
                open_menu("base.ui.menu_main")
                application:stopGame()
            end},
        }},
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