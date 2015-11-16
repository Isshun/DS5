data:extend({
    {
        label = "Iron",
        name = "base.iron",
        type = "resource",
        walkable = false,
        graphics = { path = "[base]/graphics/items/resources/raw_iron.png", type = "terrain"},
        actions = { type = "mine", cost = 1, products = {{item = "base.iron_ore", quantity = {5, 10}}} }
    },
    {
        label = "Copper",
        name = "base.copper",
        type = "resource",
        walkable = false,
        graphics = { path = "[base]/graphics/items/resources/raw_copper.png", type = "terrain"},
        actions = { type = "mine", cost = 1, products = {{item = "base.copper_ore", quantity = {5, 10}}} }
    },
})