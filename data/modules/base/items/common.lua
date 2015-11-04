data:extend({
    {
        label = "Fountain",
        name = "base.fountain",
        type = "item",
        category = "common",
        slots = {{-1, 1},{0, 1},{1, 1},{-1, 0},{1, 0},{-1, -1},{0, -1},{1, -1},},
        build = { cost = 100 },
        networks = {
            {network = "base.network.water", distance = 0}
        },
        graphics = { path = "[base]/graphics/items/fountain.png" },
        actions = {
            { type = "use", cost = 20, effects = {{type = "drink", quantity = 80}}, network = {"base.network.water"}},
            { type = "use", cost = 20, effects = {{type = "food", quantity = 80}}, network = {"base.network.water"}},
            { type = "use", cost = 20, effects = {{type = "energy", quantity = 80}}, network = {"base.network.water"}},
        }
    },
})