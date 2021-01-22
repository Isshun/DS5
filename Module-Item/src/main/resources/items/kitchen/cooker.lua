data:extend({
    {
        label = "Cooker",
        id = "base.item.kitchen.cooker",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/workshop/cooker.png" },
        size = { 3, 1 },
        health = 100,
        slots = { { 1, 1 } },
        cost = 42,
        factory = {
            slots = {
                inputs = { 0, 0 },
                outputs = { 2, 0 },
            },
            receipts = {
                { receipt = "base.receipt_easy_meal" },
                { receipt = "base.receipt_great_meal" },
                { receipt = "base.receipt_lavish_meal" },
            },
        },
        effects = {
            heat = 50,
            heatPotency = 40,
            cold = -50,
            coldPotency = 40,
        },
    },
    {
        label = "Fridge",
        id = "base.item.kitchen.fridge",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/kitchen/fridge.png", width = 48, height = 48, type = "ICON"  },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
    {
        label = "Extractor",
        id = "base.item.kitchen.extractor",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/test.png", width = 48, height = 48, type = "ICON"  },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
    {
        label = "Auto-cooker",
        id = "base.item.kitchen.auto_cooker",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/test.png", width = 48, height = 48, type = "ICON"  },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
    {
        label = "Water\ncleaner",
        id = "base.item.kitchen.water_cleaner",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/test.png", width = 48, height = 48, type = "ICON"  },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
    {
        label = "Food printer",
        id = "base.item.kitchen.food_printer",
        type = "item",
        category = "kitchen",
        graphics = {
            { path = "[base]/graphics/items/kitchen/food_printer.png", width = 48, height = 48, type = "ICON" }
        },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
    {
        label = "Protein resequencer",
        id = "base.item.kitchen.protein_resequencer",
        type = "item",
        category = "kitchen",
        graphics = {
            { path = "[base]/graphics/items/kitchen/food_replicator.png", width = 48, height = 48, type = "ICON" }
        },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
    {
        label = "Hydroponic greenhouse",
        id = "base.item.kitchen.hydroponic_greenhouse",
        type = "item",
        category = "kitchen",
        graphics = { path = "[base]/graphics/items/test.png", width = 48, height = 48, type = "ICON"  },
        size = { 1, 1 },
        health = 100,
        cost = 42,
    },
})