local window_width = 348
local window_height = 200
local header_height = 32

ui:extend({
    type = "list",
    id = "base.ui.panel_item_info",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.ItemInfoController",
    visible = false,
    views = {

        { type = "view", size = {window_width, header_height}, background = blue_light_5, views = {
            { type = "label", id = "lb_name", position = {28, 0}, text = "consumable", text_size = 22, text_color = blue_dark_1, margin = {10, 7}},
            { type = "image", id = "image", position = {4, 4}},
        }},

        { type = "view", size = {window_width, window_height}, background = blue_light_5, views = {

            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00ff },
            { type = "view", size = {1, 40}, position = {0, 0}, background = blue_light_5},
            { type = "view", size = {1, 40}, position = {347, 0}, background = blue_light_5},

            { type = "list", size = {window_width - 2, window_height - 1}, position = {1, 0}, background = blue_dark_3, views = {

                { type = "label", id = "lb_health", text = "name", text_size = 28, text_color = blue_light_5, margin = {12, 12} },

                { type = "label", text = "Dump", text_size = 12, position = {10, 8}, size = {100, 32}, action = "onDump"},

                { type = "list", id = "list_inventory" },

                { type = "list", id = "frame_build", size = {300, 300}, views = {
                    { type = "view", size = {0, 30}, views = {
                        { type = "label", text = "BUILD_IN_PROGRESS", text_size = 22, text_color = blue_light_5 },
                        { type = "label", id = "progress_build", text_size = 22, text_color = blue_light_5, position = {285, 0} },
                    }},
                    { type = "list", id = "list_build_components" },
                }},

                { type = "label", text = "onOpenComponents", size = {50, 30}, action = "onOpenComponents" },
                { type = "view", id = "base.ui.panel_item_info.details_content", special = true },

                { type = "view", id = "base.ui.panel_item_info.actions_content", special = true },

            }},
        }},

    },
})
