data:extend({
    type = "weather",
    name = "base.weather.heatwave",
    label = "Heatwave",
    icon = "[base]/graphics/icons/weather/heat.png",
    color1 = 0x750909,
    color2 = 0xf04e4e,
    unique = false,
    sun = {
        dawn = 0xddc8b2,
        twilight = 0xE79651,
        midnight = 0x181819,
        noon = 0xffffff
    },
    temperatureChange = {10, 15},
    duration = {100, 500}
})