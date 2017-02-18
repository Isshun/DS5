ui:extend({
    type = "list",
    id = "base.ui.panel_build",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    controller = "org.smallbox.faraway.client.controller.BuildController",
    visible = false,
    views = {
        { type = "label", text = "Build", text_size = 12, position = {12, 10}},
        { type = "view", size = {360, 1}, background = 0xbbbbbb, position = {12, 5}},
        { type = "grid", position = {12, 18}, columns = 1, column_width = 360, row_height = 32, views = {
            { type = "label", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 5, size = {350, 28}, action="onOpenItems", text = "Items"},
            { type = "label", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 5, size = {350, 28}, action="onOpenStructures", text = "Structures"},
            { type = "label", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 5, size = {350, 28}, action="onOpenNetworks", text = "Networks"},
        }},

        { type = "label", id = "contentLabel", text_size = 22, position = {12, 30}},
        { type = "view", id ="content", position = {12, 30}},
    }
})
