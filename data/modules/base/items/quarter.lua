data:extend({
    {
        label = "Bed",
        name = "base.bed",
        type = "item",
        category = "quarter",
        build = { cost = 100 },
        size = {1, 2},
        graphics = { path = "[base]/graphics/items/bed.png" },
        actions = { type = "use", effects = {{type = "energy", quantity = 1}} }
    },
})
