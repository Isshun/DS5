data:extend({
    label = "Corrin",
    name = "base.planet.corrin",
    type = "planet",
    class = "minshara",
    description = "Corrin description",
    graphics = {
        thumb = {path = "[base]/graphics/planets/corrin_thumb.png", credit = { author = "Adam Koebel", site = "http://nightvisions.ca/2012/08/planet-arrakis/" }},
        background = {path = "[base]/graphics/planets/corrin_bg.jpg"},
    },
    stats = {
        water = -2,
        fertility = -1,
        atmosphere = -1,
        fauna = -2,
        flora = -2,
        hostile_fauna = 2,
        hostile_humankind = 1,
    },
    day_times = {
        { hour = 5, sun = "dawn", duration = 1, light = 0.5 },
        { hour = 6, sun = "noon", duration = 2, light = 1 },
        { hour = 19, sun = "twilight", duration = 1, light = 0.5 },
        { hour = 20, sun = "midnight", duration = 2, light = 0.2 },
    },
    regions = {{
        name = "mountain",
        label = "mountain",
        color = 0x804f15,
        temperatures = {
            {floors = {-99, -1}, value = {15, 15}},
            {floors = {0, 0}, value = {20, 20}},
        },
        spots = {{latitude = {-90, 90}, frequency = 1}},
        terrains = {
            { ground = "base.ground.grass" },
            { ground = "base.ground.dirt", pattern = "ground_large" },
            { liquid = "base.liquid.water", pattern = "ground_large", condition = "ground" },
            { resource = "base.granite", ground = "base.ground.granite", pattern = "mountain" },
            { resource = "base.iron", pattern = "mineral_common_light", condition = "rock" },
        },
        weather = {
            {name = "base.weather.regular", frequency = {2, 10}, duration = {1, 1}},
        },
    }}
})