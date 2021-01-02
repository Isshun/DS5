ui:extend({
    type = "list",
    id = "base.ui.info_character.page_inventory",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoInventoryController",
    views = {
        { type = "label", text = "Inventory", text_color = blue_light_2, size = {0, 30}, text_size = 24},
        { type = "list", id = "list_inventory", position = {0, 10}, size = {0, 20}},
    }
})
