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
        { hour = 0, name = "night", color = 0xbb5588 },
        { hour = 1, name = "night", color = 0xbb5588 },
        { hour = 2, name = "night", color = 0xbb5588 },
        { hour = 3, name = "night", color = 0xbb5588 },
        { hour = 4, name = "night", color = 0xbb5588 },
        { hour = 5, name = "night", color = 0xbb5588 },
        { hour = 6, name = "night", color = 0xbb5588 },
        { hour = 7, name = "night", color = 0xbb5588 },
        { hour = 8, name = "day", color = 0xbbbb22 },
        { hour = 9, name = "day", color = 0xbbbb22 },
        { hour = 10, name = "day", color = 0xbbbb22 },
        { hour = 11, name = "day", color = 0xbbbb22 },
        { hour = 12, name = "day", color = 0xbbbb22 },
        { hour = 13, name = "day", color = 0xbbbb22 },
        { hour = 14, name = "day", color = 0xbbbb22 },
        { hour = 15, name = "day", color = 0xbbbb22 },
        { hour = 16, name = "day", color = 0xbbbb22 },
        { hour = 17, name = "day", color = 0xbbbb22 },
        { hour = 18, name = "day", color = 0xbbbb22 },
        { hour = 19, name = "day", color = 0xbbbb22 },
        { hour = 20, name = "night", color = 0xbb5588 },
        { hour = 21, name = "night", color = 0xbb5588 },
        { hour = 22, name = "night", color = 0xbb5588 },
        { hour = 23, name = "night", color = 0xbb5588 },
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
            { resource = "base.granite", ground = "base.ground.granite", pattern = "mountain", condition = "ground" },
            { resource = "base.iron", pattern = "mineral_common_light", condition = "rock" },
            { ground = "base.ground.sand", liquid = "base.liquid.water", pattern = "ground_large", condition = "ground" },
        },
        weather = {
--            {name = "base.weather.sandwhirl", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.regular", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.coldwave", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.snow", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.storm", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.lightrain", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.thunderstorm", frequency = {2, 10}, duration = {1, 1}},
        },
    }}
})