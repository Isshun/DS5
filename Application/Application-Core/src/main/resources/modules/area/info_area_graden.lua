ui:extend({
    type = "view",
    id = "base.ui.info.area.garden",
    controller = "org.smallbox.faraway.client.controller.area.AreaInfoGardenController",
    parent = "base.ui.info.area.content",
    visible = false,
    views = {
        { type = "label", text = "GARDEN", text_color = color1, text_size = 12, position = {12, 8}},
        { type = "list", id = "list_plants", position = {12, 28}},
    }
})