ui:extend({
    type = "view",
    id = "base.ui.panel_structure_info",
    controller = "org.smallbox.faraway.client.controller.StructureInfoController",
    parent = "base.ui.right_panel.sub_controller",
    visible = false,
    views = {

        { type = "view", position = {275, 15}, size = {80, 25}, background = 0x3e4b0bff, views = {
            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00ff },
            { type = "label", id = "lb_health", text_color = blue_light_5, text_size = 16, padding = 7 },
        }},

        { type = "list", position = {12, 0}, views = {
            { type = "label", text = "Structure", text_color = blue_light_2, text_size = 12, margin = {12, 0, 0, 0}},
            { type = "label", id = "lb_name", text = "name", text_size = 28, text_color = blue_light_5, margin = {12, 0} },
            { type = "list", id = "list_inventory" },

            { type = "list", id = "frame_build", size = {300, 300}, views = {
                { type = "view", size = {0, 30}, views = {
                    { type = "label", text = "BUILD_IN_PROGRESS", text_size = 22, text_color = blue_light_5 },
                    { type = "label", id = "progress_build", text_size = 22, text_color = blue_light_5, position = {285, 0} },
                }},
                { type = "list", id = "list_build_components" },
            }},
        }},
    },
})