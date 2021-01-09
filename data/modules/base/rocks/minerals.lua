data:extend({
    {
        label = "Sandstone",
        id = "base.sandstone",
        type = "resource",
        permeability = 0.1,
        walkable = false,
        graphics = {
            {path = "[base]/graphics/items/resources/sandstone.png", type = "terrain"},
            {path = "[base]/graphics/icons/material/sandstone.png", type = "icon"},
        },
        actions = { type = "mine", cost = 20, products = {{item = "base.sandstone_rubble", quantity = {0, 5}}} }
    },
    {
        label = "Calcite",
        id = "base.calcite",
        type = "resource",
        permeability = 0.1,
        walkable = false,
        graphics = {
            {path = "[base]/graphics/items/resources/calcite.png", type = "terrain"},
            {path = "[base]/graphics/icons/material/calcite.png", type = "icon"},
        },
        actions = { type = "mine", cost = 20, products = {{item = "base.calcite_rubble", quantity = {0, 5}}} }
    },
    {
        label = "Granite",
        id = "base.granite",
        type = "resource",
        permeability = 0.1,
        walkable = false,
        graphics = {
            {path = "[base]/graphics/items/resources/granite.png", type = "terrain"},
            {path = "[base]/graphics/icons/material/granite.png", type = "icon"},
        },
        actions = { type = "mine", cost = 20, products = {{item = "base.granite_rubble", quantity = {0, 5}}} }
    },
})