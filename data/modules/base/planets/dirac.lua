data:extend({
    label = "Dirac",
    name = "base.planet.dirac",
    type = "planet",
    class = "minshara",
    description = "Corrin description",
    graphics = {
        background = {path = "[base]/planets/dirac_raw.png"},
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
        { hour = 0, name = "night", color = 0xbb5588ff },
        { hour = 1, name = "night", color = 0xbb5588ff },
        { hour = 2, name = "night", color = 0xbb5588ff },
        { hour = 3, name = "night", color = 0xbb5588ff },
        { hour = 4, name = "night", color = 0xbb5588ff },
        { hour = 5, name = "night", color = 0xbb5588ff },
        { hour = 6, name = "night", color = 0xbb5588ff },
        { hour = 7, name = "night", color = 0xbb5588ff },
        { hour = 8, name = "day", color = 0xbbbb22ff },
        { hour = 9, name = "day", color = 0xbbbb22ff },
        { hour = 10, name = "day", color = 0xbbbb22ff },
        { hour = 11, name = "day", color = 0xbbbb22ff },
        { hour = 12, name = "day", color = 0xbbbb22ff },
        { hour = 13, name = "day", color = 0xbbbb22ff },
        { hour = 14, name = "day", color = 0xbbbb22ff },
        { hour = 15, name = "day", color = 0xbbbb22ff },
        { hour = 16, name = "day", color = 0xbbbb22ff },
        { hour = 17, name = "day", color = 0xbbbb22ff },
        { hour = 18, name = "day", color = 0xbbbb22ff },
        { hour = 19, name = "day", color = 0xbbbb22ff },
        { hour = 20, name = "night", color = 0xbb5588ff },
        { hour = 21, name = "night", color = 0xbb5588ff },
        { hour = 22, name = "night", color = 0xbb5588ff },
        { hour = 23, name = "night", color = 0xbb5588ff },
    },
    regions = {{
        name = "infested",
        label = "infested",
        color = 0x804f15ff,
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
    }, {
        name = "infested_innactive",
        label = "infested (innactive)",
        color = 0x804f15ff,
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