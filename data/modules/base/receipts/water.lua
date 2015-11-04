data:extend({
    {
        label = "Get water from air",
        name = "base.receipt.air_to_water",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ name = "base.consumable.water", quantity = 1}}},
        }
    },
})
