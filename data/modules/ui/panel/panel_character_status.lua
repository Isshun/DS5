ui:extend({
    type = "list",
    id = "base.ui.info_character.page_status",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoStatusController",
    views = {
        { type = "label", text = "Current occupation", text_color = blue_light_2, text_size = 24},
        { type = "view", size = {200, 40}, views = {
            { type = "label", id = "lb_job", text_size = 18, text_color = blue_light_5, padding = {10, 0}},
            { type = "label", id = "lb_job_progress", text_size = 18, text_color = blue_light_5, padding = {10, 0}, position = {250, 0}},
            { type = "view", id = "frame_job", background = blue_light_5, postion = {0, 20}, size = {0, 1}},
            { type = "image", id = "img_job", size = {32, 32}, position = {280, 0}},
        }},

        -- Needs
        { type = "view", position = {0, 14}, size = {0, 200}, views = {
            { type = "label", text = "Needs", text_color = 0x2ab8baff, size = {0, 30}, text_size = 24},
            { type = "grid", columns = 2, column_width = 182, row_height = 50, position = {0, 40}, views = {
                { type = "view", size = {170, 44}, views = {
                    { type = "label", id = "lb_need_food", text = "food", text_size = 14, text_color = 0x2ab8baff},
                    { type = "image", id = "gauge_food", position = {0, 16}, src = "[base]/graphics/needbar.png"},
                }},
                { type = "view", size = {170, 44}, views = {
                    { type = "label", id = "lb_need_drink", text = "drink", text_size = 14, text_color = 0x2ab8baff},
                    { type = "image", id = "gauge_drink", style = "base.style.gauge"},
                }},
                { type = "view", size = {170, 44}, views = {
                    { type = "label", id = "lb_need_energy", text = "energy", text_size = 14, text_color = 0x2ab8baff},
                    { type = "image", id = "gauge_energy", position = {0, 16}, src = "[base]/graphics/needbar.png"},
                }},
                { type = "view", size = {170, 44}, views = {
                    { type = "label", id = "lb_need_joy", text_size = 14, text_color = 0x2ab8baff},
                    { type = "image", id = "gauge_joy", position = {0, 16}, src = "[base]/graphics/needbar.png"},
                }},
                { type = "view", size = {170, 44}, views = {
                    { type = "label", id = "lb_need_relation", text_size = 14, text_color = 0x2ab8baff},
                    { type = "image", id = "gauge_relation", position = {0, 16}, src = "[base]/graphics/needbar.png"},
                }},
            }},
        }},

        -- Buffs
        { type = "view", position = {0, 14}, views = {
            { type = "label", text = "Buffs", text_color = 0x2ab8baff, size = {0, 30}, text_size = 24},
            { type = "list", id = "list_buffs", position = {0, 35}}
        }},
    }

})
