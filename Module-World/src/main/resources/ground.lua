data:extend({
    {
        label = "Sand",
        id = "base.ground.sand",
        type = "ground",
        color = 0xfff9bdff,
        size = {16, 16},
        fertility = 0.1,
        permeability = 0.5,
        graphics = { path = "[base]/graphics/grounds/sand.png" }
    },
    {
        label = "Grass",
        id = "base.ground.grass",
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
        id = "base.ground.dirt",
        type = "ground",
        color = 0x6d7c1dff,
        size = {16, 16},
        fertility = 0.5,
        permeability = 0.5,
        graphics = {
            { path = "[base]/graphics/grounds/dirt.png" },
            { path = "[base]/graphics/grounds/dirt_decoration.png" },
            { path = "[base]/graphics/grounds/dirt_borders.png" },
        }
    },
    {
        label = "Granite",
        id = "base.ground.granite",
        type = "ground",
        color = 0xfff9bdff,
        fertility = 0,
        permeability = 0.3,
        graphics = { path = "[base]/graphics/grounds/granite.png" }
    },
    {
        label = "Link to down stair",
        id = "base.ground.link",
        type = "ground",
        color = 0xfff9bdff,
        fertility = 0,
        permeability = 1,
        is_link_down = true,
        graphics = { path = "[base]/graphics/grounds/link_down.png" }
    },
    {
        label = "Water surface",
        id = "base.ground.water_surface",
        type = "ground",
        color = 0x8794bcff,
        fertility = 0,
        permeability = 1,
        walkable = false,
        graphics = { path = "[base]/graphics/liquids/water.png" }
    },
})