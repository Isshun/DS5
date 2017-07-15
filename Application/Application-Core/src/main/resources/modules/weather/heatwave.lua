data:extend({
    type = "weather",
    name = "base.weather.heatwave",
    label = "Heatwave",
    icon = "[base]/graphics/icons/weather/heat.png",
    color1 = 0x750909ff,
    color2 = 0xf04e4eff,
    unique = false,
    sun = {
        dawn = 0xddc8b2ff,
        twilight = 0xE79651ff,
        midnight = 0x181819ff,
        noon = 0xffffffff
    },
    temperatureChange = {10, 15},
    duration = {100, 500}
})