data:extend({
    label = "Windtrap",
    name = "base.windtrap",
    type = "item",
    category = "common",
    graphics = { path = "[base]/graphics/items/windtrap.png" },
    slots = {{-1, 1},{0, 1},{1, 1},{-1, 0},{1, 0},{-1, -1},{0, -1},{1, -1},},
    build = { cost = 100 },
    factory = {
        receipts = {
            {receipt = "base.receipt.windtrap_processing", output = "network", auto = true, cost = 50 }
        },
    },
    networks = {
        {network = "base.network.water", distance = 0}
    },
--    actions = {
--        { type = "use", cost = 20, effects = {{type = "drink", quantity = 80}}, network = {"base.network.water"}},
--    }
})