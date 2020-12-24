ui:extend({
    type = "list",
    id = "base.ui.right_panel.build",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.BuildController",
    position = {12, 0},
    visible = false,
    views = {
        { type = "label", text = "Build", text_color = color1, text_size = 12, margin = {12, 0}},
        { type = "list", id ="list"},
        { type = "label", id = "contentLabel", text_size = 22, position = {12, 30}},
    }
})
