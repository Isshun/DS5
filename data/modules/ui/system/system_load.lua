data:extend({
    type = "view",
    name = "base.ui.system_load",
    background = 0x121c1e,
    size = {application.info.screen_width, application.info.screen_height},
    visible = false,
    in_game = false,
    views = {
        { type = "view", size = {200, 200}, position = {application.info.screen_width / 2 - 80, application.info.screen_height / 2 - 40}, views = {
            { type = "image", id = "img_load", src = "[base]/graphics/icons/gear_white_64.png", size = {64, 64}, animations = { type = "rotation", duration = 3500}, position = {2, 2}},
            { type = "label", id = "lb_load", text_size = 48, text = "Load", position = {80, 10}, padding = 10 },
        }}
    },

    on_event = function(view, event, data)
        if event == "load_game.begin" then
            view:setVisible(true)
        end
        if event == "load_game.complete" then
            view:setVisible(false)
        end
    end
})