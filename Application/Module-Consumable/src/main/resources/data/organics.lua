data:extend({

    -- Vegetable
    { type = "consumable", label = "Vegetable", name = "base.vegetable", category = "organic" },
    { type = "consumable", label = "Rice", name = "base.vegetable_rice", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 13, y = 3, tile_width = 24, tile_height = 24 },
    }},
    { type = "consumable", label = "Carrot", name = "base.vegetable_carrot", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 10, y = 4, tile_width = 24, tile_height = 24 },
    }},
    { type = "consumable", label = "Wheat", name = "base.vegetable_wheat", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 11, y = 3, tile_width = 24, tile_height = 24 },
    }},
    { type = "consumable", label = "Corn", name = "base.vegetable_corn", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 9, y = 4, tile_width = 24, tile_height = 24 },
    }},
    { type = "consumable", label = "Potato", name = "base.vegetable_potato", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 5, y = 4, tile_width = 24, tile_height = 24 },
    }},
    { type = "consumable", label = "Pumpkin", name = "base.vegetable_pumpkin", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 14, y = 4, tile_width = 24, tile_height = 24 },
    }},
    { type = "consumable", label = "Tomato", name = "base.vegetable_tomato", parent = "base.vegetable", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 12, y = 4, tile_width = 24, tile_height = 24 },
    }},

    -- Seaweed
    { label = "Seaweed", name = "base.seaweed", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 0, y = 0 },
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 32, y = 0 },
    }},

    -- Seafood
    { label = "Seafood", name = "base.seafood", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 0, y = 0 },
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 32, y = 0 },
    }},

    -- Raw meat
    { label = "Raw meat", name = "base.meat", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/consumables/meat.png", x = 0, y = 0 },
    }},

    -- Insect meat
    { label = "Insect meat", name = "base.insect_meat", type = "consumable", category = "organic", stack = 500, graphics = {
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 0, y = 0 },
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 32, y = 0 },
    }},

    { label = "Desert laitue", name = "base.desert_laitue", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 0, y = 0 },
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 32, y = 0 },
    }},

    { label = "Spice", name = "base.spice", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/spice.png", type = "plant", x = 0, y = 0 },
    }},

    { label = "Red Flat Mushroom", name = "base.mushroom.red_flat_mushroom", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/plants/mushrooms.png", x = 2, y = 1 },
    }},

    { label = "Brown Round Mushroom", name = "base.mushroom.brown_round_mushroom", type = "consumable", category = "organic", graphics = {
        { path = "[module]/graphics/plants/mushrooms.png", x = 1, y = 1 },
    }},
})