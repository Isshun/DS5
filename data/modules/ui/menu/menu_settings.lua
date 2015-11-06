data:extend({
    type = "list",
    id = "base.menu.settings",
    position = {500, 400},
    size = {300, -1},
    background = 0xdd121c1e,
    visible = false,
    views = {
        { type = "label", text = "Settings", text_size = 22, padding = 16, background = 0x121c1e, size = {280, 48}, margin = {10, 0, 0, 10}},
    },

    on_event = function(view, event, data)
        if view:isVisible() and event == game.events.on_key_press and data == "ESCAPE" then
            game.ui:findById("base.menu.pause"):setVisible(true)
            view:setVisible(false)
        end
    end,
})