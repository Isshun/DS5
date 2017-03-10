tab_width = 340 / 4

ui:extend({
    type = "list",
    id = "base.ui.info_character",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoController",
    visible = false,
    views = {
        { type = "label", text = "Colon", text_color = color1, text_size = 12, margin = {12, 12, 0, 12}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, text_color = color2, margin = {12, 12} },

        { type = "view", size = {400, 40}, position = {12, 0}, views = {
            { type = "grid", columns = 10, column_width = tab_width, row_height = 40, views = {
                { type = "view", action="onOpenStatus", background = 0x359f9f, size = {tab_width, 40}, views = {
                    { type = "view", id = "bgStatus", position = {1, 1}, size = {tab_width-2, 39}, background = color3 },
                    { type = "image", src = "[base]/graphics/icons/character/ic_status.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                }},
                { type = "view", action="onOpenInventory", background = 0x359f9f, size = {tab_width, 40}, views = {
                    { type = "view", id = "bgInventory", position = {0, 1}, size = {tab_width-1, 39}, background = color3 },
                    { type = "image", src = "[base]/graphics/icons/character/ic_inventory.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                }},
                { type = "view", action="onOpenInfo", background = 0x359f9f, size = {tab_width, 40}, views = {
                    { type = "view", id = "bgDetails", position = {0, 1}, size = {tab_width-1, 39}, background = color3 },
                    { type = "image", src = "[base]/graphics/icons/character/ic_info.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                }},
                { type = "view", action="onOpenHealth", background = 0x359f9f, size = {tab_width, 40}, views = {
                    { type = "view", id = "bgHealth", position = {0, 1}, size = {tab_width-1, 39}, background = color3 },
                    { type = "image", src = "[base]/graphics/icons/character/ic_health.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                }},
            }},
        }},

        { type = "view", size = {400, 1}, background = 0x359f9f },

        { type = "view", id = "base.ui.info_character.content", special = true, position = {12, 16} },

    },

})
