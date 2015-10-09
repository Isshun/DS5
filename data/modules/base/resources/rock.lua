game.data:extend(
{
    {
        label = "Rock",
        id = "base.rock_resource",
        type = "resource",
        category = "rock",
        size = {1, 1},
        actions = {
            {
                type = "mine",
                cost = 20,
                products = {
                    { item = "base.rock_rubble", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Iron Ore",
        id = "base.iron_resource",
        type = "resource",
        category = "rock",
        size = {1, 1},
        actions = {
            {
                type = "mine",
                cost = 20,
                products = {
                    { item = "base.iron_ore", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Granite",
        id = "base.granite_resource",
        type = "resource",
        category = "rock",
        size = {1, 1},
        actions = {
            {
                type = "mine",
                cost = 20,
                products = {
                    { item = "base.granite_rubble", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
    {
        label = "Sandstone",
        id = "base.sandstone_resource",
        type = "resource",
        category = "rock",
        size = {1, 1},
        actions = {
            {
                type = "mine",
                cost = 20,
                products = {
                    { item = "base.rubble", quantity = {1, 1}, rate = 1 },
                },
            },
        },
    },
}
)
