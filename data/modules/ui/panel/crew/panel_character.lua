ui:extend({
    type = "list",
    id = "base.ui.info_character",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoController",
    visible = false,
    views = {
        { type = "view", size = {368, 56}, position = {13, 0}, views = {
            { type = "label", id = "lb_name", text_font = "font3", outlined = false, text_color = yellow, text_size = 26, size = {300, 56}, position = {0, 20}},
            { type = "label", text = "x", text_font = "whitrabt", outlined = false, text_color = blue_dark_4, background = yellow, text_size = 32, padding = {0, 5}, size = {26, 26}, position = {panel_width - 59, 14}, action = "onClose"},
        }},

        { type = "view", size = {panel_width - 32, 2}, background = yellow, position = {12, 0}},

        { type = "view", position = {12, 12}, size = {panel_width - 56, 24}, views = {
            { type = "grid", columns = 4, column_width = (panel_width - 56) / 4, row_height = 24, views = {
                { type = "label", text = "STATUS", id = "bt_status", text_font = "sui", outlined = false, text_color = blue_dark_3, background = yellow, text_size = 12, text_align = "CENTER", size = {(panel_width - 56) / 4 - 10, 24}, action="onOpenStatus"},
                { type = "label", text = "INV.", id = "bt_inventory", text_font = "sui", outlined = false, text_color = blue_dark_3, background = yellow_50, text_size = 12, text_align = "CENTER", size = {(panel_width - 56) / 4 - 10, 24}, action="onOpenInventory"},
                { type = "label", text = "HEALTH", id = "bt_health", text_font = "sui", outlined = false, text_color = blue_dark_3, background = yellow_50, text_size = 12, text_align = "CENTER", size = {(panel_width - 56) / 4 - 10, 24}, action="onOpenHealth"},
                { type = "label", text = "TIME.", id = "bt_timetable", text_font = "sui", outlined = false, text_color = blue_dark_3, background = yellow_50, text_size = 12, text_align = "CENTER", size = {(panel_width - 56) / 4 - 10, 24}, action="onOpenTimetable"},
            }},
            { type = "label", text = "?", id = "bt_info", text_font = "sui", outlined = false, text_color = blue_dark_3, background = yellow_50, text_size = 12, text_align = "CENTER", position = {panel_width - 57, 0}, size = {25, 24}, action="onOpenInfo"},
        }},

        { type = "view", id = "base.ui.info_character.content", special = true, position = {12, 16} },
    },
})
