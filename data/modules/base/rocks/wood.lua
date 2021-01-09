data:extend({
    {
        label = "Tree",
        id = "base.tree",
        type = "resource",
        size = {1, 2},
        graphics = { path = "[base]/graphics/plants/tree.png"},
        actions = {{ type = "cut", cost = 1, products = {{item = "base.consumable.wood_log", quantity = {5, 15}}}}}
    },
})
