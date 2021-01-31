ui:extend({
    visible = false,
    type = "view",
    id = "base.ui.info_character.page_inventory",
    parent = "base.ui.info_character.content",
    controller = "org.smallbox.faraway.client.controller.character.CharacterInfoInventoryController",
    views = {

        -- Equipments
        { type = "label", text = "Equipments", text_font = "font3", text_color = yellow_50, text_size = 12, size = {100, 40}, position = {0, 18}},
        { type = "grid", id = "grid_inventory", columns = 6, column_width = 56, row_height = 56, position = {0, 38}, size = {0, 200}, template = {
            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", id = "img_inventory", position = {4, 4}, size = {40, 40}}}},
            { type = "label", id = "lb_inventory", text_size = 12, text_color = 0xffffffcc, text_font = "sui", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
--            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
        }},

        -- Carrying
        { type = "label", text = "Carrying", text_font = "font3", text_color = yellow_50, size = {100, 24}, text_size = 12, position = {0, 170}},
        { type = "grid", id = "grid_carries", columns = 6, column_width = 56, row_height = 56, position = {0, 10}, size = {0, 20}, position = {0, 190}, views = {
            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
            { type = "view", size = {48, 48}, background = 0xffffff55, views = {{type = "image", src = "[base]/graphics/consumables/wood_log_40.png", position = {4, 4}, size = {40, 40}}}},
        }},

        { type = "view", size = {64, 64}, position = {10, 100}, background = 0xffffff55, views = {
            { type = "view", size = {64, 64}, position = {4, 4}, background = 0xffffff88, views = {
                { type = "image", src = "[base]/graphics/consumables/wood_log_48.png", position = {8, 8}, size = {48, 48}}
            }},
            { type = "label", text = "Head", text_size = 12, text_color = 0xffffffcc, text_font = "font3", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
        }},

        { type = "view", size = {64, 64}, position = {10, 220}, background = 0xffffff55, views = {
            { type = "view", size = {64, 64}, position = {4, 4}, background = 0xffffff88, views = {
                { type = "image", src = "[base]/graphics/consumables/wood_log_48.png", position = {8, 8}, size = {48, 48}}
            }},
            { type = "label", text = "Armor", text_size = 12, text_color = 0xffffffcc, text_font = "font3", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
        }},

        { type = "view", size = {64, 64}, position = {10, 340}, background = 0xffffff55, views = {
            { type = "view", size = {64, 64}, position = {4, 4}, background = 0xffffff88, views = {
                { type = "image", src = "[base]/graphics/consumables/wood_log_48.png", position = {8, 8}, size = {48, 48}}
            }},
            { type = "label", text = "Pants", text_size = 12, text_color = 0xffffffcc, text_font = "font3", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
        }},

        { type = "view", size = {64, 64}, position = {295, 100}, background = 0xffffff55, views = {
            { type = "view", size = {64, 64}, position = {4, 4}, background = 0xffffff88, views = {
                { type = "image", src = "[base]/graphics/consumables/wood_log_48.png", position = {8, 8}, size = {48, 48}}
            }},
            { type = "label", text = "Shoes", text_size = 12, text_color = 0xffffffcc, text_font = "font3", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
        }},

        { type = "view", size = {64, 64}, position = {295, 220}, background = 0xffffff55, views = {
            { type = "view", size = {64, 64}, position = {4, 4}, background = 0xffffff88, views = {
                { type = "image", src = "[base]/graphics/consumables/wood_log_48.png", position = {8, 8}, size = {48, 48}}
            }},
            { type = "label", text = "Gun", text_size = 12, text_color = 0xffffffcc, text_font = "font3", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
        }},

        { type = "view", size = {64, 64}, position = {295, 340}, background = 0xffffff55, views = {
            { type = "view", size = {64, 64}, position = {4, 4}, background = 0xffffff88, views = {
                { type = "image", src = "[base]/graphics/consumables/wood_log_48.png", position = {8, 8}, size = {48, 48}}
            }},
            { type = "label", text = "Cold steel", text_size = 12, text_color = 0xffffffcc, text_font = "font3", size = {72, 14}, position = {0, 70}, text_align = "CENTER"},
        }},

    }
})
