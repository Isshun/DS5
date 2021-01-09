ui:extend({
    type = "list",
    id = "base.ui.info_character.page_timetable",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoTimetableController",
    views = {
        { type = "label", text = "Timetable", text_color = blue_light_2, text_size = 24},
        { type = "view", views = {
            { type = "list", id = "list_timetable", size = {200, 40}, template = {
                { type = "view", size = {32, 22}, views = {
                    { type = "view", id = "view_timetable", size = {300, 21}},
                    { type = "label", id = "lb_timetable", text_color = color2, text_size = 14, padding = 6},
                }},
            }},
            { type = "view", id = "marker", size = {300, 1}, background = 0x88ff5868},
        }},
    }

})
