data:extend({
    label = "Arrakis",
    name = "base.planet.arrakis",
    type = "planet",
    class = "desert",
    description = "Arrakis is a hostile desertic planet, its sands contains rare resource carefully protected by strong local forces",
    graphics = { path = "[base]/graphics/planets/arrakis_thumb.png", credit = { author = "Adam Koebel", site = "http://nightvisions.ca/2012/08/planet-arrakis/" }},
    stats = {
        water = -2,
        fertility = -1,
        atmosphere = -1,
        fauna = -2,
        flora = -2,
        hostile_fauna = 2,
        hostile_humankind = 1,
    },
    hours = {
        dawn = 5,
        noon = 6,
        twilight = 19,
        midnight = 20,
    },
    regions = {{
        name = "mountain",
        label = "mountain",
        color = 0x804f15,
        temperature = {20, 40},
        spots = {{latitude = {40, 90}, frequency = 1}},
        terrains = {
            { type = "ground", name = "base.sand" },
            { type = "resource", name = "base.rock", pattern = "mountain" },
            { type = "resource", name = "base.iron", pattern = "mineral_common_light", condition = "rock" },
            { type = "resource", name = "base.raw_spice", pattern = "mineral_rare_light", condition = "ground" },
        },
        weather = {
            {name = "base.weather.sandstorm", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.el_sayal", frequency = {2, 10}, duration = {0.5, 0.5}},
        },
        fauna = {
            {name = "kulon", frequency = 0.4, count = {5, 8}},
            {name = "muad_dib", frequency = 1, count = {5, 8}},
            {name = "desert_hare", frequency = 1, count = {5, 8}},
            {name = "desert_owl", frequency = 1, count = {5, 8}},
            {name = "muad_dib", frequency = 1, count = {5, 8}},
            {name = "desert_hawk", frequency = 1, count = {5, 8}},
        },
    }, {
        name = "valley",
        label = "Valley",
        color = 0xa06f35,
        temperature = {25, 45},
        spots = {
            {latitude = {40, 90}, frequency = 0.5},
            {latitude = {-90, 40}, frequency = 0.2}},
        terrains = {
            { type = "ground", name = "base.sand" },
            { type = "resource", name = "base.rock", pattern = "valley" },
            { type = "resource", name = "base.iron", pattern = "mineral_common_light", condition = "rock" },
            { type = "resource", name = "base.raw_spice", pattern = "mineral_rare_light", condition = "ground" },
        },
        weather = {
            {name = "base.weather.sandstorm", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.el_sayal", frequency = {2, 10}, duration = {0.5, 0.5}},
        },
        fauna = {
            {name = "kulon", frequency = 0.4, count = {5, 8}},
            {name = "muad_dib", frequency = 1, count = {5, 8}},
            {name = "desert_hare", frequency = 1, count = {5, 8}},
            {name = "desert_owl", frequency = 1, count = {5, 8}},
            {name = "muad_dib", frequency = 1, count = {5, 8}},
            {name = "desert_hawk", frequency = 1, count = {5, 8}},
        },
    }, {
        name = "desert",
        label = "Desert",
        color = 0xffda3b,
        temperature = {30, 50},
        spots = {{latitude = {-90, 40}, frequency = 1}},
        terrains = {
            { type = "ground", name = "base.sand" },
            { type = "resource", name = "base.rock", pattern = "mineral_rare_large" },
            { type = "resource", name = "base.iron", pattern = "mineral_common_light", condition = "rock" },
            { type = "resource", name = "base.raw_spice", pattern = "mineral_rare_large", condition = "ground" },
            { type = "resource", name = "base.desert_laitue", pattern = "random_light", condition = "ground" },
        },
        weather = {
            {name = "base.weather.sandwhirl", frequency = {2, 10}, duration = {0.5, 2}},
            {name = "base.weather.sandstorm", frequency = {2, 10}, duration = {1, 1}},
            {name = "base.weather.el_sayal", frequency = {2, 10}, duration = {0.5, 0.5}},
        },
        fauna = {
            {name = "sandworm", frequency = 0.5, count = {1, 1}},
            {name = "kulon", frequency = 0.4, count = {5, 8}},
            {name = "muad_dib", frequency = 1, count = {5, 8}},
            {name = "desert_hare", frequency = 1, count = {5, 8}},
            {name = "desert_owl", frequency = 1, count = {5, 8}},
            {name = "muad_dib", frequency = 0.8, count = {5, 8}},
            {name = "desert_hawk", frequency = 0.8, count = {5, 8}},
        },
    }}
})