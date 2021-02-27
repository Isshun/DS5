local window_width = 400
local minimap_header_height = 51
local minimap_container_height = window_width * 0.599
local minimap_footer_height = 0
local window_height = minimap_header_height + minimap_container_height + minimap_footer_height + 4

ui:extend({
    type = "view",
    id = "base.ui.info_weather",
    size = {window_width, application.screen_height},
    controller = "org.smallbox.faraway.client.controller.InfoWeatherController",
    level = 100,
    visible = true,
    views = {

        { type = "view", id = "test", size = {600, 4}, position = {400, 220}, background = blue }

    },

})