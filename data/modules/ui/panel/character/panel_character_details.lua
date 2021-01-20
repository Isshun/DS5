ui:extend({
    visible = false,
    type = "view",
    id = "base.ui.info_character.page_info",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoDetailsController",
    views = {
        { type = "list", views = {

            -- Skill
            { type = "label", text = "Details", text_color = blue_light_2, size = {0, 30}, text_size = 24},
            { type = "list", id = "list_details"},

            -- Buffs
            { type = "view", position = {0, 14}, views = {
                { type = "label", text = "Buffs", text_color = 0x2ab8baff, size = {0, 30}, text_size = 24},
                { type = "list", id = "list_buffs", position = {0, 35}}
            }},
        }},
    }
})
