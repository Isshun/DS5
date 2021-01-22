data:extend({
    {
        label = "Masonry Workshop",
        id = "base.item.masonry_workshop",
        type = "item",
        category = "factory",
        graphics = { path = "[base]/graphics/items/workshop/masonry.png" },
        size = {3, 1},
        health = 100,
        slots = {{1, 1}},
        cost = 42,
        factory = {
            slots = {
                inputs = {0, 0},
                outputs = {2, 0},
            },
            receipts = {
                {receipt = "base.receipt_brick"},
            },
        },
    }
})