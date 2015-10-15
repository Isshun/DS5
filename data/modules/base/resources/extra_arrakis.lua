data:extend({
    {
        label = "Raw spice",
        name = "base.raw_spice",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/raw_spice.png" },
        plant = {
            mature = 5,
            growing = 0.005,
            states = {{ name = "regular", value = 1, temperature = {-100, 100}, light = {0, 100} }}
        },
        actions = { type = "gather", cost = 20, products = {{ item = "base.spice", quantity = {1, 1}, rate = 1 }}},
    },
})
