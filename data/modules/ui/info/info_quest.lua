data:extend({
    type = "list",
    name = "base.ui.info_quest",
    position = {80, 100},
    size = {372, 200},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", id = "lb_label", text = "name", text_size = 16, size = {100, 30}},
        { type = "label", id = "lb_message", text = "name", text_size = 16, size = {100, 30}},
    },

    on_event = function(view, event, data)
        if event == application.events.on_open_quest then
            view:setVisible(true)
            view:findById("lb_label"):setText(data.info.label)
            view:findById("lb_message"):setText(data.info.openMessage)
        end
    end,
})