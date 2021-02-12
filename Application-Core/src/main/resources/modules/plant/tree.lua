data:extend({
    {
        type = "plant",
        label = "Tree 1",
        id = "base.plant.tree_1",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = { 3, 10 }, light = { 0, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "regular", value = 1, temperature = { 10, 20 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "good", value = 1.25, temperature = { 20, 30 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
            }
        },
        graphics = {
            { path = "[data]/graphics/trees/tree1/tree1_raw.png", width = 453, height = 711, randomization = {scale = 0.4}, animation = { id = "wind", value = 2, speed = 0.005 }  },
        }
    },

    {
        type = "plant",
        label = "Tree 2",
        id = "base.plant.tree_2",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = { 3, 10 }, light = { 0, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "regular", value = 1, temperature = { 10, 20 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "good", value = 1.25, temperature = { 20, 30 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
            }
        },
        graphics = {
            { path = "[data]/graphics/trees/tree2/tree2_raw.png", width = 432, height = 459, animation = { id = "wind", value = 1, speed = 0.002 }  },
        }
    },

    {
        type = "plant",
        label = "Pine",
        id = "base.plant.pine",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = { 3, 10 }, light = { 0, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "regular", value = 1, temperature = { 10, 20 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "good", value = 1.25, temperature = { 20, 30 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
            }
        },
        actions = {
            type = "gather",
            cost = 20,
            products = { { item = "base.consumable.wood_log", quantity = {10, 20}, rate = 1 } }
        },
        graphics = {
            { path = "[data]/graphics/trees/pine1/pine_300.png", width = 300, height = 408, animation = { id = "wind", value = 1, speed = 0.002 }  },
        }
    },

    {
        type = "plant",
        label = "Pine 2",
        id = "base.plant.pine2",
        plant = {
            mature = 5,
            growing = 20 * 7,
            temperature = { min = 3, best = 25, max = 30 },
            states = {
                { id = "dying", value = -1 },
                { id = "depleted", value = 0.75, temperature = { 3, 10 }, light = { 0, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "regular", value = 1, temperature = { 10, 20 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
                { id = "good", value = 1.25, temperature = { 20, 30 }, light = { 0.5, 1 }, moisture = { 0, 1 }, oxygen = { 0, 1 } },
            }
        },
        graphics = {
            { path = "[data]/graphics/trees/pine2/pine_380.png", width = 380, height = 600, animation = { id = "wind", value = 1, speed = 0.005 } },
        }
    },
})