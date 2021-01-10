data:extend({

    -- Vegetable
    { type = "consumable", label = "Vegetable", id = "base.vegetable", category = "food", sub_category = "vegetable" },

    { type = "consumable", label = "Rice", id = "base.consumable.vegetable.rice", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 13, y = 3, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Carrot", id = "base.consumable.vegetable.carrot", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/foods.png", x = 1, y = 0, tile_width = 64, tile_height = 64 },
    }},

    { type = "consumable", label = "Wheat", id = "base.consumable.vegetable.wheat", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/foods.png", x = 0, y = 0, tile_width = 64, tile_height = 64 },
    }},

    { type = "consumable", label = "Corn", id = "base.consumable.vegetable.corn", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/foods.png", x = 4, y = 0, tile_width = 64, tile_height = 64 },
    }},

    { type = "consumable", label = "Potato", id = "base.consumable.vegetable.potato", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/foods.png", x = 7, y = 0, tile_width = 64, tile_height = 64 },
    }},

    { type = "consumable", label = "Pumpkin", id = "base.consumable.vegetable.pumpkin", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 14, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "consumable", label = "Tomato", id = "base.consumable.vegetable.tomato", category = "food", sub_category = "vegetable", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/foods.png", x = 7, y = 5, tile_width = 64, tile_height = 64 },
    }},

})