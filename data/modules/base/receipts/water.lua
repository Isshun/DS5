data:extend({
    {
        label = "Windtrap processing",
        id = "base.receipt.windtrap_processing",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ id = "base.consumable.water", quantity = 1}, { id = "base.consumable.electricity", quantity = 1}}},
        }
    },
})