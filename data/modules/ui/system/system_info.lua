last_temperature_value = 0

function toggle_display(view, display_name)
    local is_active = application:toggleDisplay(display_name)
    view:setFocusBackgroundColor(is_active and 0x25c8ca or 0x2c343e)
    view:setRegularBackgroundColor(is_active and 0x349394 or 0x2c3429)
    view:setBackgroundColor(is_active and 0x25c8ca or 0x2c343e)
end

data:extend({
    type = "view",
    name = "ui-test",
    size = {application.info.screen_width, 38},
    background = 0x2b3036,
    visible = true,
    views = {
        { type = "grid", columns = 20, column_width = 80, row_height = 38, views = {
            { type = "label", text = "Areas", text_size = 16, padding = 10, size = {80, 32}, background = {regular = 0x2c3429, focus = 0x2c343e}, on_click = function(v) toggle_display(v, "areas") end},
            { type = "label", text = "Room", text_size = 16, padding = 10, size = {80, 32}, background = {regular = 0x2c3429, focus = 0x2c343e}, on_click = function(v) toggle_display(v, "rooms") end},
            { type = "label", text = "Heat", text_size = 16, padding = 10, size = {80, 32}, background = {regular = 0x2c3429, focus = 0x2c343e}, on_click = function(v) toggle_display(v, "temperature") end},
            { type = "label", text = "Oxygen", text_size = 16, padding = 10, size = {80, 32}, background = {regular = 0x2c3429, focus = 0x2c343e}, on_click = function(v) toggle_display(v, "oxygen") end},
            { type = "label", text = "Water", text_size = 16, padding = 10, size = {80, 32}, background = {regular = 0x2c3429, focus = 0x2c343e}, on_click = function(v) toggle_display(v, "water") end},
            { type = "label", text = "Security", text_size = 16, padding = 10, size = {80, 32}, background = {regular = 0x2c3429, focus = 0x2c343e}, on_click = function(v) toggle_display(v, "security") end},
        }},

        { type = "view", position = {application.info.screen_width - 400, 0}, background = 0x203636, size = {400, 38}, views = {
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
        }}
    },

    on_event = function(view, event, data)
        -- Hour change
        if event == application.events.on_hour_change then
            if data then
                view:findById("lb_time"):setText(data .. "h")
            end
        end

        -- Day change
        if event == application.events.on_day_change then
            if data then
                view:findById("lb_day"):setText("jour " .. (data + 1))
            end
        end

        -- Day time change
        if event == application.events.on_day_time_change then
            if data then
                view:findById("img_time"):setImage("[base]/graphics/icons/daytimes/" .. data.sun .. ".png")
            end
        end

        -- Weather change
        if event == application.events.on_weather_change then
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
        if event == application.events.on_temperature_change then
            local value = (math.floor(data * 10) / 10)

            local img_offset = (value - last_temperature_value) < 0
                    and "[base]/graphics/icons/temperature_down_1.png"
                    or "[base]/graphics/icons/temperature_up_1.png"

            view:findById("lb_temperature"):setText((value < 0 and "" or " ") .. ((value <= -10 or value >= 10) and "" or " ") .. value .. (value == math.floor(value) and ".0" or "") .. "°")
            view:findById("img_temperature"):setImage("[base]/graphics/icons/temperature_medium.png")
            view:findById("img_temperature_offset"):setImage(img_offset)

            last_temperature_value = value
        end
    end
})