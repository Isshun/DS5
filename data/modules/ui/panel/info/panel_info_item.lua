ui:extend({
    type = "list",
    id = "base.ui.panel_item_info",
    parent = "base.ui.right_panel.sub_controller_full",
    controller = "org.smallbox.faraway.client.controller.ItemInfoController",
    visible = false,
    size = {400, 940},
    views = {

        { type = "view", background = 0x181818ff, views = {
            { type = "view", size = {392, 932}, position = {4, 4}, background = 0x262626ff, views = {

                { type = "label", id = "lb_name", text_font = "font3", text = "ground", text_size = 26, text_color = 0xb4b4b4ff, margin = {20, 16} },
                { type = "label", action = "onClose", text = "x", text_font = "whitrabt", outlined = false, text_color = 0x181818ff, background = 0xb4b4b4ff, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {349, 15}},
                { type = "view", size = {360, 1}, position = {15, 59}, background = 0xb4b4b4ff},
                { type = "view", size = {120, 120}, position = {255, 78}, background = 0xb4b4b4ff, views = {
                    { type = "image", id = "image", position = {10, 10}, size = {64, 64}},
                }},

                { type = "view", position = {0, 100}, views = {

                    { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00ff },

                    { type = "list", size = {panel_width - 2, 400}, position = {1, 0}, views = {

                        { type = "label", id = "lb_health", text = "name", text_size = 28, text_font = "sui", text_color = 0xb4b4b4ff, margin = {12, 12} },

                        -- Actions
                        { type = "grid", columns = 4, column_width = (panel_width - 56) / 4, row_height = 24, views = {
                            { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                            { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                            { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                            { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                        }},

                        { type = "list", id = "list_inventory" },

                        { type = "list", id = "frame_build", size = {300, 300}, views = {
                            { type = "view", size = {0, 30}, views = {
                                { type = "label", text = "BUILD_IN_PROGRESS", text_size = 22, text_font = "sui", text_color = 0xb4b4b4ff },
                                { type = "label", id = "progress_build", text_size = 22, text_font = "sui", text_color = 0xb4b4b4ff, position = {285, 0} },
                            }},
                            { type = "list", id = "list_build_components" },
                        }},

                        { type = "label", text = "onOpenComponents", size = {50, 30}, action = "onOpenComponents" },
                        { type = "view", id = "base.ui.panel_item_info.details_content", special = true },

                        { type = "view", id = "base.ui.panel_item_info.actions_content", special = true },

                    }},
                }},

            }},
        }},

    },
})
