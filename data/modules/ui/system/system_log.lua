data:extend({
    type = "list",
    name = "base.ui.system_log",
    size = {application.info.screen_width, 38},
    align = {"top", "left"},
    position = {0, 40},
    level = 100,
    visible = true,
    views = {
        { type = "label", id = "lb_log_1", text = "log", text_size = 16, padding = 10, size = {100, 22}},
        { type = "label", id = "lb_log_2", text = "log", text_size = 16, padding = 10, size = {100, 22}},
        { type = "label", id = "lb_log_3", text = "log", text_size = 16, padding = 10, size = {100, 22}},
        { type = "label", id = "lb_log_4", text = "log", text_size = 16, padding = 10, size = {100, 22}},
    },

    on_event = function(view, event, data)
        if event == application.events.on_log then
            view:findById("lb_log_4"):setText(view:findById("lb_log_3"):getText())
            view:findById("lb_log_3"):setText(view:findById("lb_log_2"):getText())
            view:findById("lb_log_2"):setText(view:findById("lb_log_1"):getText())
            view:findById("lb_log_1"):setText(data)
        end
    end
})