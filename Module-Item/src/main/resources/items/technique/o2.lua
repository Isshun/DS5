data:extend({
    {
        label = "O2 Recycler",
        id = "base.items.o2_recycler",
        type = "item",
        category = "Oxygen",
        size = {1, 2},
        build = {
            cost = 1000,
            components = {
                { id = "base.iron_plate", count = 100 },
                { id = "base.copper_plate", count = 50 }
            },
        },
        effects = {
            { type = "oxygen", value = 1, pressure = 100 },
        },
        actions = {
            {
                type = "craft",
                cost = 25,
                auto = true,
                label = "Craft easy meal",
                products = {item = "base.consumable.easy_meal", quantity = 1},
                inputs = {item = "base.vegetable", quantity = 1},
            },
            {
                type = "craft",
                cost = 50,
                auto = true,
                label = "Craft spice",
                products = {item = "base.spice", quantity = 1},
            },
        },
        graphics = { path = "[base]/graphics/items/o2_recycler.png" },
    }
})