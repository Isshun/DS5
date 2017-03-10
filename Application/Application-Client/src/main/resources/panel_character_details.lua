ui:extend({
    type = "view",
    id = "base.ui.info_character.page_info",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoDetailsController",
    views = {
        { type = "list", views = {
            { type = "label", text = "Personal records", text_color = color1, size = {0, 30}, text_size = 24},
            { type = "label", id = "lb_info_birth", text_color = color2, size = {0, 20}, text_size = 14},
            { type = "label", id = "lb_info_enlisted", text_color = color2, size = {0, 20}, text_size = 14},

            { type = "label", text = "Talents", text_color = color1, position = {0, 12}, size = {0, 30}, text_size = 24},
            { type = "list", id = "list_talents", position = {0, 10}},
        }},
    }
})
