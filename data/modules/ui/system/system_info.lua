ui:extend({
    type = "view",
    id = "base.ui.game_info",
    size = {application.screen_width, application.screen_height},
    controller = "org.smallbox.faraway.client.controller.SystemInfoController",
    level = 100,
    visible = true,
    views = {

        -- Top right system icons
        { type = "view", id = "view_weather", align = {"top", "right"}, position = {0, 0}, background = 0x203636ff, size = {372, 38}, views = {

            -- Time and day
            { type = "image", id = "img_time", src = "[base]/graphics/icons/daytimes/noon.png", size = {32, 32}, position = {2, 2}},
            { type = "label", id = "lb_time", text_color = color2, text_size = 16, position = {50, 7} },
            { type = "label", id = "lb_date", text_color = color2, text_size = 12, position = {38, 24} },

            -- Weather
            { type = "label", id = "lb_weather", text_color = color2, text_size = 16, text_align = "RIGHT", position = {330, 7}},
            { type = "label", id = "lb_temperature", text_color = color2, text_size = 12, text_align = "RIGHT", position = {330, 24}},
            { type = "image", id = "img_weather", src = "[base]/graphics/icons/weather/regular.png", size = {32, 32}, position = {335, 3}},

            -- Game speed
            { type = "image", id = "ic_speed", src = "[base]/graphics/ic_speed_1.png", size = {32, 32}, position = {200, 4}},
            { type = "label", id = "lb_speed", text_color = color2, text_size = 16, position = {200, 4}},

            -- Menu icon
--            { type = "image", src = "[base]/graphics/icons/menu.png", position = {335, 4}, size = {32, 32}, action = "onActionMenu"},
        }}
    },

})