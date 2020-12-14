ui:extend({
    type = "view",
    id = "base.ui.info_character.page_skills",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoSkillsController",
    views = {
        { type = "list", views = {

            -- Skill
            { type = "label", text = "Skills", text_color = color1, size = {0, 30}, text_size = 24},
            { type = "list", id = "list_skills"},

        }},
    }
})
