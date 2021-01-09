data:extend({
    {
        label = "Raw spice",
        id = "base.raw_spice",
        type = "resource",
        graphics = {
            {path = "[base]/graphics/items/resources/raw_spice_2.png", type = "terrain"},
            {path = "[base]/graphics/icons/material/spice.png", type = "icon"},
        },
        plant = {
            mature = 5,
            growing = 0.005,
            states = {{ id = "regular", value = 1, temperature = {-100, 100}, light = {0, 100} }}
        },
        actions = { type = "gather", cost = 20, products = {{ item = "base.spice", quantity = {1, 1}, rate = 1 }}},
    },
    {
        label = "Desert laitue",
        id = "base.desert_laitue",
        type = "resource",
        graphics = {
            {path = "[base]/graphics/items/resources/raw_spice_2.png", type = "terrain"},
            {path = "[base]/graphics/icons/material/spice.png", type = "icon"},
        },
        plant = {
            mature = 5,
            growing = 0.005,
            states = {{ id = "regular", value = 1, temperature = {-100, 100}, light = {0, 100} }}
        },
        actions = { type = "gather", cost = 20, products = {{ item = "base.spice", quantity = {1, 1}, rate = 1 }}},
    },
})