game.data:extend(
{
    {
        label = "Euglena Bloom",
        type = "resource",
        category = "algae",
        size = {1, 1},
        plant = {
            mature = 5,
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
        type = "resource",
        category = "algae",
        size = {1, 1},
        plant = {
            mature = 5,
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
        type = "resource",
        category = "algae",
        size = {1, 1},
        plant = {
            mature = 5,
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
        label = "Green Alagae",
        type = "resource",
        category = "algae",
        size = {1, 1},
        plant = {
            mature = 5,
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
        label = "Golden Brown Algae",
        sciLabel = "Chrysophyceae",
        type = "resource",
        category = "algae",
        size = {1, 1},
        plant = {
            mature = 5,
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
}
)
