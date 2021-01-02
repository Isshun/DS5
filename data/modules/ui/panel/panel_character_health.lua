ui:extend({
    type = "view",
    id = "base.ui.info_character.page_health",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoHealthController",
    views = {
        { type = "list", views = {

            -- Diseases
            { type = "label", text = "Diseases", text_color = blue_light_2, size = {0, 30}, text_size = 24},
            { type = "list", id = "list_diseases"},

            { type = "image", src = "[base]/graphics/icons/character/body.jpg", size = {256, 306}, position = {0, 100}},

        }},
    }

})
