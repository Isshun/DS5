data:extend({
    {
        label = "Sandstone",
        name = "base.sandstone",
        type = "resource",
        walkable = false,
        graphics = { path = "[base]/graphics/items/resources/sandstone.png", type = "terrain" },
        actions = { type = "mine", cost = 20, products = {{item = "base.sandstone_rubble", quantity = {5, 10}}} }
    },
    {
        label = "Calcite",
        name = "base.calcite",
        type = "resource",
        walkable = false,
        graphics = { path = "[base]/graphics/items/resources/calcite.png", type = "terrain" },
        actions = { type = "mine", cost = 20, products = {{item = "base.calcite_rubble", quantity = {5, 10}}} }
    },
    {
        label = "Granite",
        name = "base.granite",
        type = "resource",
        walkable = false,
        graphics = { path = "[base]/graphics/items/resources/granite.png", type = "terrain" },
        actions = { type = "mine", cost = 20, products = {{item = "base.granite_rubble", quantity = {5, 10}}} }
    },
})