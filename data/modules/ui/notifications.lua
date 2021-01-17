ui:extend({
    type = "view",
    id = "base.ui.notifications",
    controller = "org.smallbox.faraway.client.controller.NotificationController",
    align = {"top", "left"},
    position = {10, 100},
    size = {377, 300},
    views = {
        { type = "list", id = "list_notification", template = {
            {type = "label", text_size = 16, text_color = 0xffffffff, padding = {5, 2}, size = {100, 20}, margin = {2, 0}}
        }}
    }
})
