data:extend({
    {
        label = "Fountain",
        id = "base.fountain",
        type = "item",
        category = "common",
        slots = {{-1, 1},{0, 1},{1, 1},{-1, 0},{1, 0},{-1, -1},{0, -1},{1, -1},},
        build = { cost = 1 },
        networks = {
            {network = "base.network.water", distance = 0}
        },
        graphics = { path = "[base]/graphics/items/fountain.png" },
        use = { duration = 0.5, effects = {{type = "drink", quantity = 1}} },
    },
})