data:extend({

    -- Raw meat
    { label = "Raw meat", name = "base.meat", type = "consumable", category = "food", sub_category = "meat", graphics = {
        { path = "[module]/graphics/consumables/meat.png", x = 0, y = 0 },
    }},

    -- Insect meat
    { label = "Insect meat", name = "base.insect_meat", type = "consumable", category = "food", sub_category = "meat", stack = 500, graphics = {
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 0, y = 0 },
        { path = "[module]/graphics/organic_vegetable.png", type = "plant", x = 32, y = 0 },
    }},

})