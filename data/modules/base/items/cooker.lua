data:extend({
    {
        label = "Cooker",
        name = "base.cooker",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/kitchen/cooker.png" },
        size = {3, 1},
        slots = {{1, 1}},
        cost = 42,
        factory = {
            slots = {
                inputs = {0, 0},
                outputs = {2, 0},
            },
            receipts = {
                "base.receipt_easy_meal",
                "base.receipt_great_meal",
                "base.receipt_lavish_meal"
            },
        },
        effects = {
            heat = 50,
            heatPotency = 40,
            cold = -50,
            coldPotency = 40,
        },
    }
})