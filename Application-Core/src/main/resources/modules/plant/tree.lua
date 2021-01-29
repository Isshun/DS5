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
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        graphics = {
            { path = "[data]/graphics/plants/evergreen/pine_300.png", width = 300, height = 408 },
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
                { id = "depleted", value = 0.75, temperature = {3, 10}, light = {0, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "regular", value = 1, temperature = {10, 20}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
                { id = "good", value = 1.25, temperature = {20, 30}, light = {0.5, 1}, moisture = {0, 1}, oxygen = {0, 1} },
            }
        },
        graphics = {
            { path = "[data]/graphics/plants/pine/pine_380.png", width = 380, height = 600 },
        }
    },

})