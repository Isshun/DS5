ui:extend({
    type = "view",
    id = "base.ui.info_character",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoController",
    visible = false,
    views = {
        { type = "label", text = "Character", text_color = color1, text_size = 12, position = {12, 8}},
        { type = "view", size = {348, 1}, background = color1, position = {12, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {12, 37}, size = {100, 40}, text_color = color2 },

        { type = "grid", position = {12, 72}, columns = 10, column_width = 42, row_height = 42, views = {
            { type = "image", id = "btStatus", action="onOpenStatus", src = "[base]/graphics/icons/character/ic_status.png", background = 0x5588bb, size = {32, 32}},
            { type = "image", id = "btInventory", action="onOpenInventory", src = "[base]/graphics/icons/character/ic_inventory.png", background = 0x5588bb, size = {32, 32}},
            { type = "image", id = "btDetails", action="onOpenInfo", src = "[base]/graphics/icons/character/ic_info.png", background = 0x5588bb, size = {32, 32}},
            { type = "image", id = "btHealth", action="onOpenHealth", src = "[base]/graphics/icons/character/ic_health.png", background = 0x5588bb, size = {32, 32}},
        }},

        { type = "view", id = "base.ui.info_character.content", special = true, position = {12, 122} },

    },

})
