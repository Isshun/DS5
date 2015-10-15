data:extend({
    {
        label = "Sandstone",
        name = "base.sandstone",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/sandstone.png"},
        actions = { type = "mine", cost = 1, products = {{name = "base.sandstone_rubble", quantity = {5, 10}}} }
    },
    {
        label = "Calcite",
        name = "base.calcite",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/ground.png"},
        actions = { type = "mine", cost = 1, products = {{name = "base.calcite_rubble", quantity = {5, 10}}} }
    },
    {
        label = "Granite",
        name = "base.granite",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/granite.png"},
        actions = { type = "mine", cost = 1, products = {{name = "base.granite_rubble", quantity = {5, 10}}} }
    },
    {
        label = "Iron",
        name = "base.iron",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/raw_iron.png"},
        actions = { type = "mine", cost = 1, products = {{name = "base.metal_part", quantity = {5, 10}}} }
    },
    {
        label = "Carbon",
        name = "base.carbon",
        type = "resource",
        graphics = { path = "[base]/graphics/items/resources/ground.png"},
        actions = { type = "mine", cost = 1, products = {{name = "base.carbon_part", quantity = {5, 10}}} }
    },
})