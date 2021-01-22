ui:extend({
    type = "view",
    id = "base.ui.notifications",
    controller = "org.smallbox.faraway.client.controller.NotificationController",
    align = {"top", "left"},
    position = {10, 100},
    size = {377, 300},
    views = {
        { type = "list", id = "list_notification", template = {
            {type = "label", shadow = 2, shadow_color = 0x000000b4, text_font = "font3", outlined = false, text_size = 18, text_color = 0xffffffff, padding = {5, 2}, size = {100, 22}, margin = {2, 0}}
        }}
    }
})
