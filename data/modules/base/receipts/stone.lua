data:extend({
    {
        label = "Craft brick",
        name = "base.receipt_brick",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ name = "base.sandstone_brick", quantity = 5}}, inputs = {{ name = "base.sandstone_rubble", quantity = 10}}},
            {outputs = {{ name = "base.calcite_brick", quantity = 5}}, inputs = {{ name = "base.calcite_rubble", quantity = 10}}},
            {outputs = {{ name = "base.granite_brick", quantity = 5}}, inputs = {{ name = "base.granite_rubble", quantity = 10}}},
        }
    },
})