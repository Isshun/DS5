data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 38},
    background = 0x2b3036,
    visible = true,
    views = {
        { type = "image", id = "img_time", src = "[base]/graphics/icons/sun.png", size = {32, 32}, position = {2, 2}},
        { type = "label", id = "lb_time", text_size = 18, position = {28, 3}, padding = 10 },
        { type = "label", id = "lb_day", text_size = 18, position = {67, 3}, padding = 10 },
        --        { type = "label", id = "lb_light", text_size = 18, position = {200, 3}, padding = 10 },
        { type = "image", id = "img_weather", size = {32, 32}, position = {160, 3}},
        { type = "label", id = "lb_weather", text_size = 18, position = {188, 3}, padding = 10 },
        --        { type = "image", src = "[base]/graphics/icons/menu.png", size = {32, 32}, position = {363, 3}},
        { type = "label", id = "lb_temperature", text = "38°", text_size = 18, position = {328, 3}, padding = 10 },
        { type = "image", src = "[base]/graphics/icons/temperature_medium.png", size = {24, 24}, position = {370, 7}},
    },

    on_refresh =
    function(view)
        view:findById("img_time"):setImage(game.hour > 6 and game.hour < 20 and "[base]/graphics/icons/sun.png" or "[base]/graphics/icons/moon.png")
        view:findById("lb_time"):setText(game.hour .. "h")
        view:findById("lb_day"):setText("jour " .. (game.day+1))
        --        view:findById("lb_light"):setText("light: " .. (game.world:getLight()))
    end,

    on_event = function(view, event, data)
        if event == game.events.on_weather_change then
            if data then
                print (data.icon)
                view:findById("lb_weather"):setVisible(true)
                view:findById("lb_weather"):setText(data.label)
                view:findById("img_weather"):setImage(data.icon)
            else
                view:findById("lb_weather"):setVisible(false)
                view:findById("img_weather"):setVisible(false)
            end
        end
    end
})