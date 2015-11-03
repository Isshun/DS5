last_temperature_value = 0

data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 0},
    size = {400, 38},
    background = 0x2b3036,
    visible = true,
    views = {
        { type = "image", id = "img_time", src = "[base]/graphics/icons/sun.png", size = {32, 32}, position = {2, 2}},
        { type = "label", id = "lb_time", text_size = 16, position = {28, 3}, padding = 10 },
        { type = "label", id = "lb_day", text_size = 16, position = {63, 3}, padding = 10 },
        --        { type = "label", id = "lb_light", text_size = 18, position = {200, 3}, padding = 10 },
        { type = "image", id = "img_weather", size = {32, 32}, position = {160, 3}},
        { type = "label", id = "lb_weather", text_size = 16, position = {188, 3}, padding = 10 },
        --        { type = "image", src = "[base]/graphics/icons/menu.png", size = {32, 32}, position = {363, 3}},
        { type = "label", id = "lb_temperature", text_size = 16, position = {300, 3}, padding = 10 },
        { type = "image", id = "img_temperature", size = {24, 24}, position = {368, 7}},
        { type = "image", id = "img_temperature_offset", size = {12, 24}, position = {383, 7}},
    },

    on_event = function(view, event, data)
        -- Hour change
        if event == game.events.on_hour_change then
            if data then
                view:findById("lb_time"):setText(data .. "h")
            end
        end

        -- Day change
        if event == game.events.on_day_change then
            if data then
                view:findById("lb_day"):setText("jour " .. (data + 1))
            end
        end

        -- Day time change
        if event == game.events.on_day_time_change then
            if data then
                view:findById("img_time"):setImage("[base]/graphics/icons/daytimes/" .. data.sun .. ".png")
            end
        end

        -- Weather change
        if event == game.events.on_weather_change then
            if data then
                view:findById("lb_weather"):setVisible(true)
                view:findById("lb_weather"):setText(data.label)
                view:findById("img_weather"):setImage(data.icon)
            else
                view:findById("lb_weather"):setVisible(false)
                view:findById("img_weather"):setVisible(false)
            end
        end

        -- Temperature change
        if event == game.events.on_temperature_change then
            if data then
                local value = (math.floor(data * 10) / 10)

                local img_offset = (value - last_temperature_value) < 0
                        and "[base]/graphics/icons/temperature_down_1.png"
                        or "[base]/graphics/icons/temperature_up_1.png"

                view:findById("lb_temperature"):setVisible(true)
                view:findById("lb_temperature"):setText((value < 0 and "" or " ") .. ((value <= -10 or value >= 10) and "" or " ") .. value .. (value == math.floor(value) and ".0" or "") .. "�")
                view:findById("img_temperature"):setImage("[base]/graphics/icons/temperature_medium.png")
                view:findById("img_temperature_offset"):setImage(img_offset)

                last_temperature_value = value
            else
                view:findById("lb_temperature"):setVisible(false)
                view:findById("img_temperature"):setVisible(false)
                view:findById("img_temperature_offset"):setVisible(false)
            end
        end
    end
})