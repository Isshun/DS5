ui:extend({
    type = "list",
    id = "base.ui.info_character.page_timetable",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoTimetableController",
    views = {
        { type = "label", text = "Timetable", text_color = color1, text_size = 24},
        { type = "list", id = "list_timetable", size = {200, 40}, views = {
        }},
    }

})
