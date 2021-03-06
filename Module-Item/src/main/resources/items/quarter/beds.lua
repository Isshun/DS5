data:extend({
    { label = "Wood Bed",
        id = "base.item.bed.wood",
        type = "item",
        category = "furniture",
        build = { cost = 20 },
        size = {1, 2},
        health = 75,
        graphics = { path = "[base]/graphics/items/bed_solo_128.png" },
        receipts = {label = "Wood", icon = "[base]/graphics/icons/material/wood.png", components = { item = "base.consumable.wood_log", quantity = 10}},
        use = { duration = 6, effects = {{type = "energy", quantity = 1}} }
    },
    { label = "Sandstone Bed",
        id = "base.item.bed.sandstone",
        type = "item",
        category = "furniture",
        build = { cost = 100 },
        size = {1, 2},
        health = 75,
        graphics = { path = "[base]/graphics/items/bed_solo_128.png" },
        receipts = {label = "Sandstone", icon = "[base]/graphics/icons/material/sandstone.png", components = { item = "base.sandstone_brick", quantity = 10}},
        use = { duration = 6, effects = {{type = "energy", quantity = 1}} }
    },
})
