data:extend({
    type = "weather",
    name = "base.weather.heatwave",
    label = "Heatwave",
    icon = "[base]/graphics/icons/weather/heat.png";
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