ui:extend({
    type = "list",
    id = "base.ui.panel_item_info",
    parent = "base.ui.right_panel.sub_controller_full",
    controller = "org.smallbox.faraway.client.controller.ItemInfoController",
    visible = false,
    size = {400, 940},
    views = {

        { type = "view", background = blue_dark_3, views = {
            { type = "view", size = {400 - 6, 940 - 6}, background = 0x181818ff, position = {3, 3}, views = {
                { type = "list", size = {392, 932}, position = {1, 1}, background = 0x262626ff, views = {

                    -- Header
                    { type = "view", size = {367, 60}, views = {
                        { type = "label", id = "lb_name", text_font = "font3", text = "ground", text_size = 26, text_color = 0xb4b4b4ff, margin = {20, 16} },
                        { type = "label", action = "onClose", text = "x", text_font = "whitrabt", outlined = false, text_color = 0x181818ff, background = 0xb4b4b4ff, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {349, 15}},
                    }},

                    { type = "view", size = {360, 1}, position = {15, 0}, background = 0xb4b4b4ff},

                    { type = "view", size = {200, 400}, position = {0, 18}, views = {

                        -- Info panel
                        { type = "list", views = {

                            -- Durability
                            { type = "view", id = "frame_durability", size = {222, 45}, position = {13, 0}, views = {
                                { type = "view", id = "progress_health", size = {224, 25}, background = 0x89ab0088 },
                                { type = "label", text = "Durability", size = {222, 25}, position = {5, 0}, text_size = 18, text_font = "sui", text_align = "LEFT", text_color = 0xe5e5e5ff, shadow = 1 },
                                { type = "label", id = "lb_health", size = {222, 25}, position = {-5, 0}, text_size = 18, text_font = "sui", text_align = "RIGHT", text_color = 0xe5e5e5ff, shadow = 1 },
                            }},

                            -- Build progress
                            { type = "view", id = "frame_build", size = {222, 45}, position = {13, 0}, views = {
                                { type = "view", id = "view_progress", background = 0xffffff88, size = {360, 24} },
                                { type = "label", text = "Building...", size = {222, 25}, position = {5, 0}, text_size = 18, text_font = "sui", text_align = "LEFT", text_color = 0xe5e5e5ff, shadow = 1 },
                                { type = "label", id = "lb_build", size = {222, 25}, position = {-5, 0}, text_size = 18, text_font = "sui", text_align = "RIGHT", text_color = 0xe5e5e5ff, shadow = 1 },
                            }},

                            -- Contains
                            { type = "view", size = {222, 45}, position = {15, 0}, views = {
                                { type = "label", text = "Contains", text_font = "sui", text_size = 20, text_color = 0xb4b4b4ff, size = {300, 40}, text_align = "LEFT" },
                                { type = "list", id = "list_inventory", position = {0, 38}, size = {fill, content}, template = {
                                    { type = "label", id = "lb_item", text_align = "LEFT", text_font = "sui", text_size = 14, text_color = 0xb4b4b4ff, size = {300, 24}}
                                }},
                            }},

                        }},

                        -- Image
                        { type = "view", size = {120, 120}, position = {255, 0}, background = 0xb4b4b4ff, views = {
                            { type = "image", id = "image", position = {10, 10}, size = {64, 64}},
                        }},

                    }},

                    { type = "view", position = {0, 150}, views = {

                        { type = "list", size = {panel_width - 2, 400}, position = {1, 0}, views = {

                            -- Actions
                            { type = "grid", columns = 4, column_width = (panel_width - 56) / 4, row_height = 24, views = {
                                { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                                { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                                { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                                { type = "label", text = "Dump", text_color = 0x181818ff, background = 0xb4b4b4ff, text_font = "sui", text_size = 12, position = {10, 8}, text_align = "CENTER", size = {72, 32}, action = "onDump"},
                            }},

                        }},
                    }},

                }},

            }},

        }},

    },
})
