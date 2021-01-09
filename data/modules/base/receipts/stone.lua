data:extend({
    {
        label = "Craft brick",
        id = "base.receipt_brick",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ id = "base.sandstone_brick", quantity = 5}}, inputs = {{ id = "base.sandstone_rubble", quantity = 10}}},
            {outputs = {{ id = "base.calcite_brick", quantity = 5}}, inputs = {{ id = "base.calcite_rubble", quantity = 10}}},
            {outputs = {{ id = "base.granite_brick", quantity = 5}}, inputs = {{ id = "base.granite_rubble", quantity = 10}}},
        }
    },
})