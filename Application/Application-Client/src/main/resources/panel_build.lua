ui:extend({
    type = "list",
    id = "base.ui.right_panel.build",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.BuildController",
    position = {12, 0},
    visible = false,
    views = {
        { type = "label", text = "Build", text_color = color1, text_size = 12, margin = {12, 0}},
        { type = "list", columns = 1, views = {
            { type = "label", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 5, size = {350, 28}, action="onOpenItems", text = "Items"},
            { type = "list", id ="list_items", visible = false},
            { type = "label", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 5, size = {350, 28}, action="onOpenStructures", text = "Structures"},
            { type = "list", id ="list_structures", visible = false},
            { type = "label", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 5, size = {350, 28}, action="onOpenNetworks", text = "Networks"},
            { type = "list", id ="list_networks", visible = false},
        }},

        { type = "label", id = "contentLabel", text_size = 22, position = {12, 30}},
    }
})
