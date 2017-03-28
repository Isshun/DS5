data:extend({

    -- Vegetable
    { type = "consumable", label = "Vegetable", name = "base.vegetable", category = "food", sub_category = "vegetable" },

    { type = "consumable", label = "Rice", name = "base.consumable.vegetable.rice", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 13, y = 3, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Carrot", name = "base.consumable.vegetable.carrot", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 10, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Wheat", name = "base.consumable.vegetable.wheat", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 11, y = 3, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Corn", name = "base.consumable.vegetable.corn", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 9, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Potato", name = "base.consumable.vegetable.potato", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 5, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Pumpkin", name = "base.consumable.vegetable.pumpkin", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 14, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Tomato", name = "base.consumable.vegetable.tomato", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/plants/tomato.png", x = 4, y = 0 },
    }},

})