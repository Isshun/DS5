data:extend({
    {
        label = "Cooker",
        name = "base.cooker",
        type = "item",
        category = "kitchen",
        size = {3, 1},
        cost = 42,
        receipts = {
            "base.receipt_easy_meal",
            "base.receipt_great_meal",
            "base.receipt_lavish_meal"
        },
        effects = {
            heat = 50,
            heatPotency = 40,
            cold = -50,
            coldPotency = 40,
        },
    }
})