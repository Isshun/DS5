data:extend({
    type = "weather",
    name = "base.weather.snow",
    icon = "[base]/graphics/icons/weather/snow.png";
    label = "Snow",
    particle = "snow",
    sun = {
        dawn = 0xddc8b2,
        twilight = 0xE79651,
        midnight = 0x181819,
        noon = 0xffffff
    },
    temperatureChange = {-5, 0},
    duration = {100, 200},
    conditions = {
        temperature = {-40, 5}
    }
})