consumable = nil

ui:extend({
    type = "list",
    id = "base.ui.info_plant",
    controller = "org.smallbox.faraway.client.controller.PlantInfoController",
    parent = "base.ui.right_panel",
    level = 10,
    visible = false,
    position = {10, 10},
    views = {
        { type = "label", text = "Plant", text_color = 0x679B99, text_size = 12},
        { type = "view", size = {380, 1}, background = 0x679B99},

        { type = "label", id = "lb_label", text_color = 0xB4D4D3, text_size = 28, padding = {10, 0}, size = {100, 40}},

        { type = "label", id = "lb_maturity", text_color = 0xB4D4D3, text_size = 16},
        { type = "label", id = "lb_garden", text_color = 0xB4D4D3, text_size = 16},
        { type = "label", id = "lb_seed", text_color = 0xB4D4D3, text_size = 16},
        { type = "label", id = "lb_nourish", text_color = 0xB4D4D3, text_size = 16},
        { type = "label", id = "lb_job", text_color = 0xB4D4D3, text_size = 16},
        { type = "label", id = "lb_growing", text_color = 0xB4D4D3, text_size = 16},

        { type = "view", size = {380, 1}, views = {

            { type = "view", size = {380, 1}, position = {10, 10}, views = {
                { type = "label", text = "Temperature", text_color = 0x679B99, text_size = 16 },
                { type = "label", id = "lb_min_temperature", text_color = 0x679B99, text_size = 12, position = {0, 16} },
                { type = "label", id = "lb_max_temperature", text_color = 0x679B99, text_size = 12, position = {120, 16} },
                { type = "label", id = "lb_temperature", text_color = 0x679B99, text_size = 12, position = {60, 16} },
                { type = "image", src = "[base]/graphics/icons/plant_cursor.png", size = {128, 16}, position = {0, 24} },
                { type = "view", id = "img_temperature", size = {6, 18}, background = 0xffffff, position = {0, 24}}
            }},

            { type = "view", size = {380, 1}, position = {200, 10}, views = {
                { type = "label", text = "Light", text_color = 0x679B99, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor.png", size = {128, 16}, position = {0, 22} },
                { type = "view", id = "img_light", size = {6, 18}, background = 0xffffff, position = {0, 22}},
            }},

            { type = "view", size = {380, 1}, position = {10, 110}, views = {
                { type = "label", text = "Moisture", text_color = 0x679B99, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor.png", size = {128, 16}, position = {0, 22} },
                { type = "view", id = "img_moisture", size = {6, 18}, background = 0xffffff, position = {0, 22}},
            }},

            { type = "view", size = {380, 1}, position = {200, 110}, views = {
                { type = "label", text = "Oxygen", text_color = 0x679B99, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor.png", size = {128, 16}, position = {0, 22} },
                { type = "view", id = "img_oxygen", size = {6, 18}, background = 0xffffff, position = {0, 22}},
            }},

        }},

    }
})