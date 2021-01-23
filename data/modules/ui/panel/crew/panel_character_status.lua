ui:extend({
    visible = false,
    type = "list",
    id = "base.ui.info_character.page_status",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoStatusController",
    views = {

        -- Current job
        { type = "label", text = "Current occupation", text_font = "font3", text_color = yellow_50, text_size = 12, size = {100, 40}, position = {0, 18}},
        { type = "view", size = {200, 50}, views = {
            { type = "label", id = "lb_job", text = "Use wood bed", text_font = "font3", outlined = false, text_color = 0xffffffdd, text_size = 20, size = {300, 30}, position = {4, 5}},
            { type = "view", id = "gauge_progress", background = 0xffffff55, position = {0, 0}, size = {367, 24}},
            { type = "image", id = "img_job", size = {32, 32}, position = {280, 0}},
        }},

        -- Needs
        { type = "label", text = "Needs", text_font = "font3", text_color = yellow_50, size = {100, 24}, text_size = 12},
        { type = "view", position = {0, 0}, size = {0, 178}, views = {
            { type = "grid", id = "grid_needs", columns = 2, column_width = 190, row_height = 42, position = {0, 0}, template = {
                { type = "view", views = {
                    { type = "label", id = "lb_name", text_font = "font3", text_size = 12, text_color = blue_light_5, position = {0, 0}},
                    { type = "label", id = "lb_value", text_font = "font3", text_size = 12, text_color = blue_light_5, position = {132, 0}},
                    { type = "view", background = 0x02191bff, size = {176, 12}, position = {0, 14}},
                    { type = "view", id = "img_gauge", background = 0xffffffdd, size = {120, 12}, position = {0, 14}},
                    { type = "image", visible = false, id = "img_gauge", src = "[base]/graphics/needbar.png", position = {0, 14}},
                }}
            }},
        }},

        -- Buffs
        { type = "label", text = "Effects", text_font = "font3", text_color = yellow_50, size = {100, 24}, text_size = 12},
        { type = "view", position = {0, 0}, size = {0, 200}, views = {
            { type = "list", id = "list_buffs", columns = 2, column_width = 190, row_height = 42, position = {0, 0}, template = {
                { type = "label", id = "lb_name", text_font = "font3", text_size = 12, text_color = 0xffffffdd, position = {0, 0}, size = {100, 20}},
            }},
        }},

    }

})
