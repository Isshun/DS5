ui:extend({
    type = "view",
    id = "base.ui.right_panel.crew",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.CrewController",
    visible = false,
    debug = true,
    views = {
        { type = "label", text = "Crew", text_color = blue_light_2, text_size = 28, position = {12, 16}},
        { type = "list", id = "list_crew", position = {12, 52}, template = {
            { type = "view", size = {200, 60}, views = {
                { type = "label", id = "lb_character_name", text_color = blue_light_4, text_size = 18, size = {300, 28}, padding = {8, 0}},
                { type = "label", id = "lb_character_job", text_color = blue_light_4, text_size = 18, size = {300, 28}, position = {0, 22}, padding = {8, 0}},

                { type = "view", position = {270, 10}, views = {
                    {type = "view", size = {10, 30}, position = {1, 0}, background = 0x0D4D4Bff},
                    {type = "view", id = "gauge_food", size = {}, background = 0x679B99ff},
                    {type = "image", src = "[base]/graphics/needs/ic_food.png", size = {12, 12}, position = {0, 34}},
                }},

                { type = "view", position = {270 + 20, 10}, views = {
                    {type = "view", size = {10, 30}, position = {1, 0}, background = 0x0D4D4Bff},
                    {type = "view", id = "gauge_health", size = {}, background = 0x679B99ff},
                    {type = "image", src = "[base]/graphics/needs/ic_health.png", size = {12, 12}, position = {0, 34}},
                }},

                { type = "view", position = {270 + 40, 10}, views = {
                    {type = "view", size = {10, 30}, position = {1, 0}, background = 0x0D4D4Bff},
                    {type = "view", id = "gauge_social", size = {}, background = 0x679B99ff},
                    {type = "image", src = "[base]/graphics/needs/ic_social.png", size = {12, 12}, position = {0, 34}},
                }},

                { type = "view", position = {270 + 60, 10}, views = {
                    {type = "view", size = {10, 30}, position = {1, 0}, background = 0x0D4D4Bff},
                    {type = "view", id = "gauge_entertainment", size = {}, background = 0x679B99ff},
                    {type = "image", src = "[base]/graphics/needs/ic_entertainment.png", size = {12, 12}, position = {0, 34}},
                }},
            }},
        }},
    },

})