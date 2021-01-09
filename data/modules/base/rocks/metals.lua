data:extend({
    {
        label = "Iron",
        id = "base.iron",
        type = "resource",
        permeability = 0.1,
        walkable = false,
        graphics = {
            {path = "[base]/graphics/items/resources/raw_iron.png", type = "terrain"},
            {path = "[base]/graphics/icons/material/iron.png", type = "icon"},
        },
        actions = { type = "mine", cost = 1, products = {{item = "base.iron_ore", quantity = {5, 10}}} }
    },
    {
        label = "Copper",
        id = "base.copper",
        type = "resource",
        permeability = 0.1,
        walkable = false,
        graphics = {
            {path = "[base]/graphics/items/resources/raw_copper.png", type = "terrain" },
            {path = "[base]/graphics/icons/material/copper.png", type = "icon"},
        },
        actions = { type = "mine", cost = 1, products = {{item = "base.copper_ore", quantity = {5, 10}}} }
    },
})