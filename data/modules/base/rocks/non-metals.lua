data:extend({
    {
        label = "Carbon",
        id = "base.carbon",
        type = "resource",
        walkable = false,
        graphics = { path = "[base]/graphics/items/resources/carbon.png" },
        actions = { type = "mine", cost = 1, products = {{item = "base.carbon_part", quantity = {5, 10}}} }
    },
})