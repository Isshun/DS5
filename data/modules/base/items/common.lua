data:extend({
    {
        label = "Fountain",
        name = "base.fountain",
        type = "item",
        category = "common",
        slots = {{-1, 1},{0, 1},{1, 1},{-1, 0},{1, 0},{-1, -1},{0, -1},{1, -1},},
        build = { cost = 100 },
        graphics = { path = "[base]/graphics/items/fountain.png" },
        actions = { type = "use", cost = 20, effects = {{type = "drink", quantity = 80}} }
    },
})
