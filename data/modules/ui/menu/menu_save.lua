data:extend({
    type = "list",
    id = "base.ui.menu_save",
    position = {500, 400},
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "label", text = "Save", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}, on_click = function() open_menu("base.menu.save") end},
    },

    on_event = function(view, event, data)
        if view:isVisible() and event == application.events.on_key_press and data == "ESCAPE" then
            application.ui:findById(application.game and "base.ui.menu_pause" or "base.ui.menu_main"):setVisible(true)
            view:setVisible(false)
        end
    end,
})