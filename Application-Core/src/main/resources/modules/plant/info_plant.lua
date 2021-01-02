consumable = nil

ui:extend({
    type = "list",
    id = "base.ui.info_plant",
    controller = "org.smallbox.faraway.client.controller.PlantInfoController",
    parent = "base.ui.right_panel.sub_controller",
    level = 10,
    visible = false,
    position = {10, 10},
    views = {
        { type = "label", text = "Plant", text_color = blue_light_2, text_size = 22},

        { type = "label", id = "lb_label", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_maturity", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_garden", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_seed", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_nourish", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_job", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_growing", text_color = blue_light_5, text_size = 16},

        { type = "label", text = "Parcel", text_color = blue_light_2, text_size = 22, margin = {60, 0, 0, 0}},
        { type = "view", size = {380, 120}, views = {

            { type = "view", position = {10, 15}, views = {
                { type = "label", text = "Temperature", text_color = blue_light_2, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor.png", size = {128, 8}, position = {0, 18} },
                { type = "label", id = "lb_current_temperature", text_color = blue_light_2, text_size = 12 },
                { type = "view", id = "img_temperature", size = {6, 12}, background = 0xffffffff }
            }},

            { type = "view", position = {200, 15}, views = {
                { type = "label", text = "Light", text_color = blue_light_2, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor_light.png", size = {128, 8}, position = {0, 18} },
                { type = "label", id = "lb_current_light", text_color = blue_light_2, text_size = 12 },
                { type = "view", id = "img_light", size = {6, 12}, background = 0xffffffff},
            }},

            { type = "view", position = {10, 60}, views = {
                { type = "label", text = "Moisture", text_color = blue_light_2, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor_moisture.png", size = {128, 8}, position = {0, 18} },
                { type = "label", id = "lb_current_moisture", text_color = blue_light_2, text_size = 12 },
                { type = "view", id = "img_moisture", size = {6, 12}, background = 0xffffffff },
            }},

            { type = "view", position = {200, 60}, views = {
                { type = "label", text = "Oxygen", text_color = blue_light_2, text_size = 16 },
                { type = "image", src = "[base]/graphics/icons/plant_cursor_oxygen.png", size = {128, 8}, position = {0, 18} },
                { type = "label", id = "lb_current_oxygen", text_color = blue_light_2, text_size = 12 },
                { type = "view", id = "img_oxygen", size = {6, 12}, background = 0xffffffff },
            }},

        }},

        { type = "label", text = "Encyclopedie", text_color = blue_light_2, text_size = 22, margin = {60, 0, 0, 0}},
        { type = "label", id = "lb_temperature", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_light", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_moisture", text_color = blue_light_5, text_size = 16},
        { type = "label", id = "lb_oxygen", text_color = blue_light_5, text_size = 16},

    }
})