data:extend({
    type = "list",
    name = "debug_character_buff",
    position = {200, 20},
    size = {400, 600},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", text = "Buff", text_size = 16, padding = 10, size = {400, 30}, background = 0x333333},
        { type = "label", id = "lb_duration", text_size = 14, padding = 10, size = {400, 30}},
    },

    on_refresh = function(view)
        if g_buff and g_buff.luaData.duration then
            view:findById("lb_duration"):setText("Duration: " .. g_buff.luaData.duration)
        end
    end,

    on_event = function(view, event, data)
        if event == "debug.open_buff" then
            view:setVisible(view:isVisible())
            g_buff = data
        end
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
        end
    end,
})