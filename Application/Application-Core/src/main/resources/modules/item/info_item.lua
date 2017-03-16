ui:extend({
    type = "view",
    name = "base.ui.panel_item_info",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.ItemInfoController",
    visible = false,
    views = {

        { type = "view", position = {275, 15}, size = {80, 25}, background = 0x3e4b0b, views = {
            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00 },
            { type = "label", id = "lb_health", text_color = color2, text_size = 16, padding = 7 },
        }},

        { type = "list", views = {

            { type = "label", text = "Item", text_color = color1, text_size = 12, margin = {12, 12, 0, 12}},
            { type = "label", id = "lb_name", text = "name", text_size = 28, text_color = color2, margin = {12, 12} },

            { type = "label", text = "Dump", text_size = 12, position = {10, 8}, size = {100, 32}, action = "onDump"},

            { type = "list", id = "list_inventory" },

            { type = "label", text = "onOpenComponents", size = {50, 30}, action = "onOpenComponents" },
            { type = "view", id = "base.ui.panel_item_info.details_content", special = true },

        }},
    },
})
