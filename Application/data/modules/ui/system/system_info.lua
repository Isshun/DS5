last_temperature_value = 0

ui:extend({
    type = "view",
    name = "base.ui.game_info",
    size = {application.info.screen_width, 38},
    controller = "org.smallbox.faraway.client.controller.SystemInfoController",
    background = 0x2b3036,
    level = 100,
    visible = true,
    views = {

        -- Top left ressources icons
        { type = "view", id = "view_resource", size = {372, 34}, views = {
            { type = "grid", id = "grid_resource", columns = 8, column_width = 70, position = {0, 2}, views = {
                { type = "view", size = {70, 32}, position = {0, 0}, views = {
                    { type = "image", src = "[base]/graphics/icons/food.png", size = {32, 32}},
                    { type = "label", id = "lb_resource_food", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                }},
                { type = "view", size = {70, 32}, position = {0, 0}, views = {
                    { type = "image", src = "[base]/graphics/icons/water.png", size = {32, 32}},
                    { type = "label", id = "lb_resource_water", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                }},
                { type = "view", size = {70, 32}, views = {
                    { type = "image", src = "[base]/graphics/icons/wood.png", size = {32, 32}},
                    { type = "label", id = "lb_resource_wood", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                }},
                { type = "view", size = {70, 32}, views = {
                    { type = "image", src = "[base]/graphics/icons/wood.png", size = {32, 32}},
                    { type = "label", id = "lb_resource_granite", text = "-1", text_size = 16, padding = 10, position = {24, 0}, size = {32, 32}},
                }},
            }},
        }},

        -- Top right speed icon
        { type = "image", id = "ic_speed", align = {"top", "right"}, src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {372, 4}},

        -- Menu icon
        { type = "image", src = "[base]/graphics/icons/menu.png", align = {"top", "right"}, position = {472, 4}, size = {32, 32}},

        -- Top right system icons
        { type = "view", align = {"top", "right"}, position = {0, 0}, background = 0x203636, size = {372, 38}, views = {

            -- Time and day
            { type = "image", id = "img_time", src = "[base]/graphics/icons/daytimes/noon.png", size = {32, 32}, position = {2, 2}},
            { type = "label", id = "lb_time", text = "hr", text_color = 0xB4D4D3, text_size = 16, position = {28, 3}, padding = 10 },
            { type = "label", id = "lb_day", text = "day", text_color = 0xB4D4D3, text_size = 16, position = {63, 3}, padding = 10 },

            -- Weather
            { type = "image", id = "img_weather", src = "[base]/graphics/icons/weather/regular.png", size = {32, 32}, position = {160, 3}},
            { type = "label", id = "lb_weather", text = "weather", text_color = 0xB4D4D3, text_size = 16, position = {188, 3}, padding = 10 },

            -- Temperature
            { type = "label", id = "lb_temperature", text = "tmp", text_color = 0xB4D4D3, text_size = 16, position = {300, 3}, padding = 10 },
            { type = "image", id = "img_temperature", src = "[base]/graphics/icons/temperature_medium.png", size = {24, 24}, position = {350, 7}},
            { type = "image", id = "img_temperature_offset", size = {12, 24}, position = {383, 7}},
        }}
    },

    on_refresh = function(view)
        local network_module = application:getModule("NetworkModule")
        if network_module then
            local water = 0
            local iterator = network_module:getNetworks():iterator()
            while iterator:hasNext() do
                local network = iterator:next()
                if network:getInfo().name == "base.network.water" then water = water + network:getQuantity() end
            end
            view:findById("lb_resource_water"):setText(math.floor(water));
        end

        local resource_module = application:getModule("ResourceModule")
        if resource_module then
            view:findById("lb_resource_food"):setText(resource_module:getFoodCount());
            view:findById("lb_resource_wood"):setText(resource_module:getConsumableCount("base.consumable.wood_log"));
            view:findById("lb_resource_granite"):setText(resource_module:getConsumableCount("base.granite_brick"));
        end
    end,

    on_event = function(view, event, data)
        -- Speed change
        if event == application.events.on_speed_change then
--            view:findById("lb_speed"):setText(data == 0 and "||" or ("x" .. data));
        end

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