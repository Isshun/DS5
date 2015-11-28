data:extend({
    type = "view",
    name = "base.ui.info_weather",
    position = {0, 920},
    size = {372, 200},
    background = 0x121c1e,
    visible = true,
    views = {
        { type = "list", position = {10, 10}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 16, size = {100, 30}},
            { type = "label", id = "lb_temperature", text_size = 12},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_display_change and data[1] == "debug" then
            view:setVisible(data[2])
        end
    end,

    on_refresh = function(view)
        local weather_module = application:getModule("WeatherModule")
        if weather_module then
            view:findById("lb_name"):setText(weather_module:getWeather() and weather_module:getWeather().label or "")

            local str_temp = ""
            for i = application.game:getInfo().worldFloors - 1, 0, -1 do
                str_temp = str_temp .. i .. "=" .. weather_module:getTemperature(i) .. " "
            end
            view:findById("lb_temperature"):setText("Temp", ": ", str_temp)
        end
    end
})