data:extend({
    {
        label = "Sand",
        name = "base.ground.sand",
        type = "ground",
        color = 0xfff9bd,
        fertility = 0.1,
        permeability = 0.5,
        graphics = { path = "[base]/graphics/grounds/sand.png" }
    },
    {
        label = "Grass",
        name = "base.ground.grass",
        type = "ground",
        color = 0x87943cff,
        size = {16, 16},
        fertility = 1,
        permeability = 0.5,
        graphics = {
            { path = "[base]/graphics/grounds/grass.png" },
            { path = "[base]/graphics/grounds/grass_decoration.png" },
        }
    },
    {
        label = "Dirt",
        name = "base.ground.dirt",
        type = "ground",
        color = 0xfff9bd,
        size = {16, 16},
        fertility = 0.5,
        permeability = 0.5,
        graphics = {
            { path = "[base]/graphics/grounds/dirt.png" },
            { path = "[base]/graphics/grounds/dirt_decoration.png" },
        }
    },
    {
        label = "Granite",
        name = "base.ground.granite",
        type = "ground",
        color = 0xfff9bd,
        fertility = 0,
        permeability = 0.3,
        graphics = { path = "[base]/graphics/grounds/granite.png" }
    },
    {
        label = "Link to down stair",
        name = "base.ground.link",
        type = "ground",
        color = 0xfff9bd,
        fertility = 0,
        permeability = 1,
        is_link_down = true,
        graphics = { path = "[base]/graphics/grounds/link_down.png" }
    },
})