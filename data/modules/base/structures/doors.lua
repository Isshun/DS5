data:extend({
    { label = "Wood door",
        name = "base.wood_door",
        type = "structure",
        category = "door",
        health = 200,
        sealing = 0.7,
        walkable = true,
        door = true,
        graphics = { path = "[base]/graphics/items/structures/door_wood.png", type = "structure" },
        receipts = {{components = { item = "base.wood_log", quantity = 10}}}},
    { label = "Sandstone door",
        name = "base.sandstone_door",
        type = "structure",
        category = "door",
        health = 250,
        walkable = true,
        door = true,
        receipts = {{components = { item = "base.sandstone_brick", quantity = 10}}}},
    { label = "Calcite door",
        name = "base.calcite_door",
        type = "structure",
        category = "door",
        health = 350,
        walkable = true,
        door = true,
        receipts = {{components = { item = "base.calcite_brick", quantity = 10}}}},
    { label = "Granite door",
        name = "base.granite_door",
        type = "structure",
        category = "door",
        health = 500,
        walkable = true,
        door = true,
        receipts = {{components = { item = "base.granite_brick", quantity = 10}}}},
    { label = "Metal door",
        name = "base.metal_door",
        type = "structure",
        category = "door",
        health = 450,
        walkable = true,
        door = true,
        receipts = {{components = { item = "base.iron_plate", quantity = 10}}}},
    { label = "Carbon Fiber door",
        name = "base.carbon_fiber_door",
        type = "door",
        walkable = true,
        door = true,
        category = "structure",
        health = 800,
        receipts = {{components = { item = "base.carbon_fiber_part", quantity = 10}}}},
})
