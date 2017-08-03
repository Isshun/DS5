ui:extend({
    type = "view",
    id = "base.ui.panel_item_info",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.ItemInfoController",
    visible = false,
    views = {

        { type = "view", position = {275, 15}, size = {80, 25}, background = 0x3e4b0bff, views = {
            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00ff },
            { type = "label", id = "lb_health", text_color = color2, text_size = 16, padding = 7 },
        }},

        { type = "list", views = {

            { type = "label", text = "Item", text_color = color1, text_size = 12, margin = {12, 12, 0, 12}},
            { type = "label", id = "lb_name", text = "name", text_size = 28, text_color = color2, margin = {12, 12} },

            { type = "label", text = "Dump", text_size = 12, position = {10, 8}, size = {100, 32}, action = "onDump"},

            { type = "list", id = "list_inventory" },

            { type = "list", id = "frame_build", size = {300, 300}, views = {
                { type = "view", size = {0, 30}, views = {
                    { type = "label", text = "BUILD_IN_PROGRESS", text_size = 22, text_color = color2 },
                    { type = "label", id = "progress_build", text_size = 22, text_color = color2, position = {285, 0} },
                }},
                { type = "list", id = "list_build_components" },
            }},

            { type = "label", text = "onOpenComponents", size = {50, 30}, action = "onOpenComponents" },
            { type = "view", id = "base.ui.panel_item_info.details_content", special = true },

            { type = "view", id = "base.ui.panel_item_info.actions_content", special = true },

        }},
    },
})
