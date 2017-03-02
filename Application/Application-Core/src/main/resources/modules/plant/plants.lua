data:extend({

    {
        type = "plant",
        label = "Rice",
        name = "base.plant.rice",
        plant = {
            mature = 5,
            growing = 0.005,
            states = {
                { name = "dying", value = 1 },
                { name = "depleted", value = 1, temperature = {3, 10}, light = {0, 100}, moisture = {0, 100}, oxygen = {0, 100} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {50, 100}, moisture = {0, 100}, oxygen = {0, 100} },
                { name = "good", value = 1, temperature = {20, 30}, light = {50, 100}, moisture = {0, 100}, oxygen = {0, 100} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.spice", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 13, y = 3, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Carrot", name = "base.plant.carrot", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 10, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "plant", label = "Wheat", name = "base.plant.wheat", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 11, y = 3, tile_width = 24, tile_height = 24 },
    }},

    { type = "plant", label = "Corn", name = "base.plant.corn", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 9, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "plant", label = "Potato", name = "base.plant.potato", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 5, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "plant", label = "Pumpkin", name = "base.plant.pumpkin", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 14, y = 4, tile_width = 24, tile_height = 24 },
    }},

    { type = "plant", label = "Tomato", name = "base.plant.tomato", graphics = {
        { path = "[module]/graphics/consumables/vegetables.png", x = 12, y = 4, tile_width = 24, tile_height = 24 },
    }},

})