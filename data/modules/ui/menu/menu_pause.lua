data:extend({
    type = "view",
    id = "base.ui.menu_pause",
    size = {application.info.screen_width, application.info.screen_height},
    background = 0x000000,
    in_game = false,
    visible = false,
    views = {
        { type = "label", text = "Stand-by", text_size = 42, text_color = 0x25c9cb, position = {20, application.info.screen_height - 52}},
        { type = "view", id = "bt_cursor", background = 0x25c9cb, size = {20, 32}, position = {210, application.info.screen_height - 52}},
        { type = "list", position = {application.info.screen_width / 2 - 280 / 2, application.info.screen_height / 2 - 240 / 2}, views = {
            { type = "label", text = "Resume", text_size = 22, text_color = 0xffffff, padding = 16, background = {regular = 0x55ffffff, focus = 0x25c9cb}, size = {280, 48}, margin = {10, 0, 0, 10}},
            { type = "label", text = "Save", text_size = 22, text_color = 0xffffff, padding = 16, background = {regular = 0x55ffffff, focus = 0x25c9cb}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_save") end},
            { type = "label", text = "Load", text_size = 22, text_color = 0xffffff, padding = 16, background = {regular = 0x55ffffff, focus = 0x25c9cb}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_load") end},
            { type = "label", text = "Settings", text_size = 22, text_color = 0xffffff, padding = 16, background = {regular = 0x55ffffff, focus = 0x25c9cb}, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.ui.menu_settings") end},
            { type = "label", text = "Quit", text_size = 22, text_color = 0xffffff, padding = 16, background = {regular = 0x55ffffff, focus = 0x25c9cb}, size = {280, 48}, margin = {10, 0, 10, 10}, on_click = function()
                open_menu("base.ui.menu_main")
                application:stopGame()
            end},
        }},
    },

    on_refresh = function(view, frame)
        if frame % 40 == 0 then
            view:findById("bt_cursor"):setVisible(not view:findById("bt_cursor"):isVisible())
        end
    end,

    on_event = function(view, event, data)
        if event == application.events.on_game_paused then
            print("open")
            view:setVisible(true)
            application.ui:findById("base.ui.panel_main"):setVisible(false)
        end
        if event == application.events.on_game_resume then
            print("bye")
            view:setVisible(false)
            application.ui:findById("base.ui.panel_main"):setVisible(true)
        end
    end,
})

function open_menu(menu_name)
    application.ui:findById("base.ui.menu_pause"):setVisible(false)
    application.ui:findById(menu_name):setVisible(true)
end