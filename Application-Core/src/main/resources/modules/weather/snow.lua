data:extend({
    type = "weather",
    id = "base.weather.snow",
    icon = "[base]/graphics/icons/weather/snow.png",
    color1 = 0x206161ff,
    color2 = 0x7bd3d3ff,
    label = "Snow",
    particle = "snow",
    sun = {
        dawn = 0xddc8b2ff,
        twilight = 0xE79651ff,
        midnight = 0x181819ff,
        noon = 0xffffffff
    },
    temperatureChange = {-5, 0},
    duration = {100, 200},
    conditions = {
        temperature = {-40, 5}
    }
})