data:extend({
    {
        label = "Wood",
        name = "base.wood",
        type = "resource",
        size = {1, 2},
        graphics = { path = "[base]/graphics/items/resources/wood.png"},
        actions = {{ type = "cut", cost = 1, products = {{item = "base.wood_log", quantity = {5, 15}}}}}
    },
})
