data:extend({
    {
        label = "Windtrap processing",
        name = "base.receipt.windtrap_processing",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ name = "base.consumable.water", quantity = 1}, { name = "base.consumable.electricity", quantity = 1}}},
        }
    },
})