ui:extend({
    type = "view",
    id = "base.ui.right_panel.crew",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.CrewController",
    visible = false,
    debug = true,
    views = {
        { type = "label", text = "Crew", text_color = color1, text_size = 28, position = {12, 16}},
--        { type = "image", src = "[base]/graphics/needs/lb_food.png", size = {12, 32}, position = {282, 16}},
--        { type = "image", src = "[base]/graphics/needs/lb_food.png", size = {12, 32}, position = {282 + 20, 16}},
--        { type = "image", src = "[base]/graphics/needs/lb_food.png", size = {12, 32}, position = {282 + 40, 16}},
--        { type = "image", src = "[base]/graphics/needs/lb_food.png", size = {12, 32}, position = {282 + 60, 16}},
        { type = "list", id = "list_crew", position = {12, 52}},
    },

})