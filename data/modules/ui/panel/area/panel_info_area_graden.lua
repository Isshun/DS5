local window_pos = 346
local tab_width = 396 / 5 - 10

ui:extend({
    type = "view",
    id = "base.ui.info_garden",
    parent = "base.ui.right_panel.sub_controller_full",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoGardenController",
    visible = false,
    size = {panel_width, 940},
    views = {

        { type = "view", size = {panel_width, application.screen_height - window_pos - 10}, background = green, views = {
            { type = "view", size = {panel_width - 8, application.screen_height - window_pos - 10 - 8}, position = {4, 4}, background = blue_dark_4, views = {
                { type = "label", id = "lb_name", text_font = "font3", text = "Area #42", text_size = 26, text_color = 0xd4d4d4ff, margin = {20, 16} },
                { type = "label", action = "onClose", text = "x", text_font = "whitrabt", outlined = false, text_color = 0x181818ff, background = 0xd4d4d4ff, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {349, 15}},
                { type = "view", size = {360, 1}, position = {15, 59}, background = 0xb4b4b4ff},

                { type = "label", text = "Prority", text_font = "font3", text_color = green, text_size = 10, size = {panel_width - 26, 10}, position = {15, 68}},
                { type = "grid", columns = 5, column_width = (panel_width - 26) / 5, row_height = 24, position = {15, 78}, views = {
                    { type = "label", text = "1", id = "btPriority1", text_color = blue_dark_4, text_font = "sui", text_size = 18, text_align = "CENTER", size = {tab_width, 24}, background = 0x87d10042 },
                    { type = "label", text = "2", id = "btPriority2", text_color = blue_dark_4, text_font = "sui", text_size = 18, text_align = "CENTER", size = {tab_width, 24}, background = green },
                    { type = "label", text = "3", id = "btPriority3", text_color = blue_dark_4, text_font = "sui", text_size = 18, text_align = "CENTER", size = {tab_width, 24}, background = 0x87d10042 },
                    { type = "label", text = "4", id = "btPriority4", text_color = blue_dark_4, text_font = "sui", text_size = 18, text_align = "CENTER", size = {tab_width, 24}, background = 0x87d10042 },
                    { type = "label", text = "5", id = "btPriority5", text_color = blue_dark_4, text_font = "sui", text_size = 18, text_align = "CENTER", size = {tab_width, 24}, background = 0x87d10042 },
                }},

                { type = "list", id = "list_plants", position = {14, 118}, template = {
                    { type = "view", size = {360, 24}, views = {
                        { type = "label", id = "bt_open", text = "+", text_font = "conthrax-sb", text_color = 0xb4b4b4ff, text_size = 15, padding = {5, 0}, size = {20, 28} },
                        { type = "label", id = "lb_item", text = "Consumables", text_font = "conthrax-sb", text_color = 0xffffffff, text_size = 15, padding = {5, 0}, size = {300, 28}, position = {20, 0} },
                        { type = "image", id = "img_active", src = "[base]/graphics/icons/ic_ok.png", size = {16, 16}, position = {360 - 20, 0} },
                        { type = "list", id = "list_consumable", position = {0, 24}, template = {
                            { type = "view", size = {360, 24}, views = {
                                { type = "label", id = "lb_item", text = "Consumables", text_font = "conthrax-sb", text_color = 0xb4b4b4ff, text_size = 15, padding = {5, 0}, size = {300, 28}, position = {20, 0} },
                                { type = "image", id = "img_active", src = "[base]/graphics/icons/ic_ok.png", size = {16, 16}, position = {360 - 20, 0} },
                            }},
                        }}
                    }},
                }},
            }},
        }},

    }
})