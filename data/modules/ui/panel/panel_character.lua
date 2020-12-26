tab_width = 348 / 6

ui:extend({
    type = "list",
    id = "base.ui.info_character",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoController",
    visible = false,
    views = {
        { type = "view", size = {348, 25}, background = blue_light_5, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 16, text_color = blue_dark_1, margin = {7, 5}},
            { type = "label", id = "lb_position", text_color = blue_dark_1, text_size = 12, margin = {9, 12, 0, 12}, position = {270, 0}},
        }},

        { type = "view", size = {348, 500}, background = blue_light_5, views = {

            { type = "view", size = {348, 40}, background = blue_dark_1, position = {0, 0}, views = {
                { type = "grid", columns = 10, column_width = tab_width, row_height = 40, views = {
                    { type = "view", action="onOpenStatus", size = {tab_width, 40}, views = {
                        { type = "view", id = "bgStatus", position = {1, 1}, size = {tab_width-2, 39}},
                        { type = "image", src = "[base]/graphics/icons/character/ic_status.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                    }},
                    { type = "view", action="onOpenInventory", size = {tab_width, 40}, views = {
                        { type = "view", id = "bgInventory", position = {0, 1}, size = {tab_width-1, 39}},
                        { type = "image", src = "[base]/graphics/icons/character/ic_inventory.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                    }},
                    { type = "view", action="onOpenInfo", size = {tab_width, 40}, views = {
                        { type = "view", id = "bgDetails", position = {0, 1}, size = {tab_width-1, 39}},
                        { type = "image", src = "[base]/graphics/icons/character/ic_info.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                    }},
                    { type = "view", action="onOpenSkill", size = {tab_width, 40}, views = {
                        { type = "view", id = "bgSkills", position = {0, 1}, size = {tab_width-1, 39}},
                        { type = "image", src = "[base]/graphics/icons/character/ic_skill.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                    }},
                    { type = "view", action="onOpenHealth", size = {tab_width, 40}, views = {
                        { type = "view", id = "bgHealth", position = {0, 1}, size = {tab_width-1, 39}},
                        { type = "image", src = "[base]/graphics/icons/character/ic_health.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                    }},
                    { type = "view", action="onOpenTimetable", size = {tab_width, 40}, views = {
                        { type = "view", id = "bgTimetable", position = {0, 1}, size = {tab_width-1, 39}},
                        { type = "image", src = "[base]/graphics/icons/character/ic_clock.png", size = {32, 32}, position = {tab_width / 2 - 16, 5}},
                    }},
                }},
            }},

            { type = "view", size = {1, 40}, position = {0, 0}, background = blue_light_5},
            { type = "view", size = {1, 40}, position = {347, 0}, background = blue_light_5},

            { type = "view", size = {346, 459}, position = {1, 40}, background = blue_dark_3, views = {
                { type = "view", id = "base.ui.info_character.content", special = true, position = {12, 16} },
            }},
        }},


    },

})
