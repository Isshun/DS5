game.data:extend({
    {
        label = "Iron",
        name = "base.iron",
        type = "resource",
        graphics = "[base]/graphics/items/resources/raw_iron.png",
        actions = { type = "mine", cost = 1, products = {{name = "base.iron_part", quantity = {5, 10}}} }
    },
    {
        label = "Copper",
        name = "base.copper",
        type = "resource",
        graphics = "[base]/graphics/items/resources/raw_copper.png",
        actions = { type = "mine", cost = 1, products = {{name = "base.copper_part", quantity = {5, 10}}} }
    },
})