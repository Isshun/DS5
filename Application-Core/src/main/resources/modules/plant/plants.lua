data:extend({

    {
        type = "plant",
        label = "Rice",
        id = "base.plant.rice",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.rice", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/plants/rice.png", x = 0, y = 0 },
        }
    },

    { type = "plant", label = "Carrot", id = "base.plant.carrot",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.carrot", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/64/carrot_1.png", x = 0, y = 0, tile_width = 64, tile_height = 64 },
        }
    },

    { type = "plant", label = "Wheat", id = "base.plant.wheat",
        plant = {
            grid = 4,
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.wheat", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[data]/graphics/plants/wheat/wheat_128.png", width = 64, height = 140, randomization = { offset = 10, flip = true }, animation = { id = "wind", value = 1, speed = 0.01 } },
        }
    },

    { type = "plant", label = "Laitue", id = "base.plant.laitue",
        plant = {
            grid = 4,
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.wheat", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[data]/graphics/plants/laitue/laitue_64_2.png", width = 64, height = 64, randomization = { offset = 6, rotate = 100 } },
        }
    },

    { type = "plant", label = "Corn", id = "base.plant.corn",
        plant = {
            grid = 2,
            mature = 5,
            growing = 0.005,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.corn", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[data]/graphics/plants/corn/corn_128.png", width = 96, height = 264, randomization = { offset = 10, flip = true }, animation = { id = "wind", value = 4, speed = 0.003 } },
        }
    },

    { type = "plant", label = "Potato", id = "base.plant.potato",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.potato", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/foods.png", x = 7, y = 0, tile_width = 64, tile_height = 64 },
        }
    },

    { type = "plant", label = "Pumpkin", id = "base.plant.pumpkin",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.pumpkin", quantity = {1, 1}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/vegetables.png", x = 14, y = 4, tile_width = 24, tile_height = 24 },
        }
    },

    { type = "plant", label = "Tomato", id = "base.plant.tomato",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        actions = {
            type = "gather", cost = 20, products = {{ item = "base.consumable.vegetable.tomato", quantity = {4, 6}, rate = 1 } }
        },
        graphics = {
            { path = "[module]/graphics/consumables/foods.png", x = 7, y = 5, tile_width = 64, tile_height = 64 },
        }
    },

})