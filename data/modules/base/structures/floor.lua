data:extend({
    { label = "Wooden floor",
        name = "base.wood_floor",
        type = "structure",
        category = "floor",
        floor = {
            speed = 1
        },
        health = 200,
        walkable = true,
        graphics = { path = "[base]/graphics/items/structures/floor_wood.png", type = "structure" },
        receipts = {{components = {{ item = "base.wood_log", quantity = 5}}}}},
    { label = "Concrete floor",
        name = "base.concrete_floor",
        type = "structure",
        category = "floor",
        floor = {
            speed = 1
        },
        health = 250,
        walkable = true,
        graphics = { path = "[base]/graphics/items/structures/floor_concrete.png", type = "structure" },
        receipts = {
            {components = {{ item = "base.sandstone_brick", quantity = 5}}},
            {components = {{ item = "base.calcite_brick", quantity = 5}}},
            {components = {{ item = "base.granite_brick", quantity = 5}}},
        }},
    { label = "Metal floor",
        name = "base.metal_floor",
        type = "structure",
        category = "floor",
        floor = {
            speed = 1
        },
        health = 250,
        walkable = true,
        graphics = { path = "[base]/graphics/items/structures/floor_metal.png", type = "structure" },
        receipts = {{components = {{ item = "base.iron_plate", quantity = 5}}}}},
})
