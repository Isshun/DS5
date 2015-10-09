game.data:extend(
{
    {
        label = "Euglena Bloom",
        id = "base.algae_1",
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
        id = "base.algae_2",
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
        id = "base.algae_3",
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
        id = "base.algae_4",
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
        id = "base.algae_5",
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
