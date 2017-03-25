ui:extend({
    type = "view",
    name = "base.ui.game_info",
    size = {application.info.screen_width, 38},
    controller = "org.smallbox.faraway.client.controller.SystemInfoController",
    level = 100,
    visible = true,
    views = {

        -- Top right system icons
        { type = "view", align = {"top", "right"}, position = {0, 0}, background = 0x203636, size = {372, 38}, views = {

            -- Time and day
            { type = "image", id = "img_time", src = "[base]/graphics/icons/daytimes/noon.png", size = {32, 32}, position = {2, 2}},
            { type = "label", id = "lb_time", text_color = color2, text_size = 16, position = {50, 7} },
            { type = "label", id = "lb_date", text_color = color2, text_size = 12, position = {38, 24} },

            -- Weather
            { type = "image", id = "img_weather", src = "[base]/graphics/icons/weather/regular.png", size = {32, 32}, position = {130, 3}},
            { type = "label", id = "lb_weather", text_color = color2, text_size = 16, position = {168, 7}},
            { type = "label", id = "lb_temperature", text_color = color2, text_size = 12, position = {168, 24}},

            -- Game speed
            { type = "image", id = "ic_speed", src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {300, 4}},
            { type = "label", id = "lb_speed", text_color = color2, text_size = 16, position = {300, 4}},

            -- Menu icon
            { type = "image", src = "[base]/graphics/icons/menu.png", position = {335, 4}, size = {32, 32}},
        }}
    },

})