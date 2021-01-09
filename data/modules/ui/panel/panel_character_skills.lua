ui:extend({
    type = "view",
    id = "base.ui.info_character.page_skills",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoSkillsController",
    views = {
        { type = "list", views = {

            -- Skill
            { type = "label", text = "Skills", text_color = blue_light_2, size = {0, 30}, text_size = 24},
            { type = "list", id = "list_skills", template = {
                { type = "view", border = 0x359f9fff, margin = {8, 0}, size = {320, 28}, views = {
                    { type = "label", id = "lb_skill", text_color = 0x359f9fff, text_size = 16, position = {8, 16}, size = {320, 28}},
                    { type = "image", id = "gauge_skill", src = "[base]/graphics/needbar.png"},
                }}
            }},

        }},
    }
})
