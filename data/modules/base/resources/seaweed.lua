data:extend({
    {
        label = "Euglena bloom",
        name = "base.seaweed1",
        graphics = { path = "[base]/graphics/items/resources/desert_berry.png" },
        --graphics = "[base]/graphics/items/resources/seaweed1.png",
        type = "resource",
        category = "algae",
        plant = {
            gather = 0.25,
            growing = 0.005,
            states = {
                { name = "depleted",    value = -0.1,   temperature = {-999, 999},  light = {-999, 999} },
                { name = "stasis",      value = 0,      temperature = {0, 50},      light = {0, 100} },
                { name = "partial",     value = 0.5,    temperature = {14, 50},     light = {0, 100} },
                { name = "regular",     value = 1,      temperature = {20, 50},     light = {0, 100} },
                { name = "exceptional", value = 1.25,   temperature = {25, 50},     light = {0, 100} },
            },
        },
        actions = {
            {
                type = "gather",
                cost = 20,
                products = {
                    { item = "base.seaweed", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Chlorella",
        name = "base.seaweed2",
        graphics = { path = "[base]/graphics/items/resources/seaweed2.png" },
        type = "resource",
        category = "algae",
        plant = {
            gather = 0.25,
            growing = 0.005,
            states = {
                { name = "depleted",    value = -0.1,   temperature = {-999, 999},  light = {-999, 999} },
                { name = "stasis",      value = 0,      temperature = {0, 50},      light = {0, 100} },
                { name = "partial",     value = 0.5,    temperature = {14, 50},     light = {0, 100} },
                { name = "regular",     value = 1,      temperature = {20, 50},     light = {0, 100} },
                { name = "exceptional", value = 1.25,   temperature = {25, 50},     light = {0, 100} },
            },
        },
        actions = {
            {
                type = "gather",
                cost = 20,
                products = {
                    { item = "base.seaweed", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Eel grass",
        sciLabel = "Zostera marina",
        name = "base.seaweed3",
--        graphics = "[base]/graphics/items/resources/seaweed3.png",
        graphics = { path = "[base]/graphics/items/resources/desert_green_plant.png" },
--        graphics = "[base]/graphics/items/resources/desert_green_plant.png",
        type = "resource",
        category = "algae",
        size = {1, 1},
        light = { power = .5, radius = 5 },
        plant = {
            gather = 0.25,
            growing = 0.005,
            states = {
                { name = "depleted",    value = -0.1,   temperature = {-999, 999},  light = {-999, 999} },
                { name = "stasis",      value = 0,      temperature = {0, 50},      light = {0, 100} },
                { name = "partial",     value = 0.5,    temperature = {14, 50},     light = {0, 100} },
                { name = "regular",     value = 1,      temperature = {20, 50},     light = {0, 100} },
                { name = "exceptional", value = 1.25,   temperature = {25, 50},     light = {0, 100} },
            },
        },
        actions = {
            {
                type = "gather",
                cost = 20,
                products = {
                    { item = "base.seaweed", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Green algae",
        name = "base.seaweed4",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/seaweed4.png" },
--        graphics = "[base]/graphics/items/resources/seaweed4.png",
        category = "algae",
        size = {1, 1},
        plant = {
            gather = 0.25,
            growing = 0.005,
            states = {
                { name = "depleted",    value = -0.1,   temperature = {-999, 999},  light = {-999, 999} },
                { name = "stasis",      value = 0,      temperature = {0, 50},      light = {0, 100} },
                { name = "partial",     value = 0.5,    temperature = {14, 50},     light = {0, 100} },
                { name = "regular",     value = 1,      temperature = {20, 50},     light = {0, 100} },
                { name = "exceptional", value = 1.25,   temperature = {25, 50},     light = {0, 100} },
            },
        },
        actions = {
            {
                type = "gather",
                cost = 20,
                products = {
                    { item = "base.seaweed", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Golden brown algae",
        sciLabel = "Chrysophyceae",
        graphics = { path = "[base]/graphics/items/resources/seaweed5.png" },
        name = "base.seaweed5",
        type = "resource",
        category = "algae",
        size = {1, 1},
        plant = {
            gather = 0.25,
            growing = 0.005,
            states = {
                { name = "depleted",    value = -0.1,   temperature = {-999, 999},  light = {-999, 999} },
                { name = "stasis",      value = 0,      temperature = {0, 50},      light = {0, 100} },
                { name = "partial",     value = 0.5,    temperature = {14, 50},     light = {0, 100} },
                { name = "regular",     value = 1,      temperature = {20, 50},     light = {0, 100} },
                { name = "exceptional", value = 1.25,   temperature = {25, 50},     light = {0, 100} },
            },
        },
        actions = {
            {
                type = "gather",
                cost = 20,
                products = {
                    { item = "base.seaweed", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
})
