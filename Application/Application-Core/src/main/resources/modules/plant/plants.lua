data:extend({

    {
        type = "plant",
        label = "Rice",
        name = "base.plant.rice",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.rice", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/plants/rice.png", x = 0, y = 0 },
        }
    },

    { type = "plant", label = "Carrot", name = "base.plant.carrot",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.carrot", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 10, y = 4, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Wheat", name = "base.plant.wheat",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.wheat", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 11, y = 3, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Corn", name = "base.plant.corn",
        plant = {
            mature = 5,
            growing = 0.005,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.corn", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 9, y = 4, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Potato", name = "base.plant.potato",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.potato", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 5, y = 4, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Pumpkin", name = "base.plant.pumpkin",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.pumpkin", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 14, y = 4, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Tomato", name = "base.plant.tomato",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { name = "dying", value = -1 },
                { name = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { name = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.tomato", quantity = {4, 6}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/plants/tomato.png" },
        }
    },

})