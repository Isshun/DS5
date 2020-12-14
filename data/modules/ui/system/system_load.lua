ui:extend({
    type = "view",
    id = "base.ui.system_load",
    visible = false,
    in_game = false,
    views = {
        { type = "view", id = "bg_load", size = {application.screen_width, application.screen_height}, background = color3},
        { type = "view", size = {200, 200}, position = {application.screen_width / 2 - 100, application.screen_height / 2 - 40}, views = {
            { type = "image", id = "img_load", src = "[base]/graphics/icons/gear_white_64.png", size = {64, 64}, animations = { type = "rotation", duration = 3500}, position = {2, 2}},
            { type = "label", id = "lb_load", text_size = 48, text = "Load", position = {80, 10}, padding = 10 },
        }}
    },

    on_event = function(view, event, data)
        if event == "load_game.begin" then
            view:setVisible(true)
            view:findById("bg_load"):setVisible(true)
            view:findById("lb_load"):setText("Load")
        end
        if event == "load_game.complete" then
            view:setVisible(false)
        end
        if event == "save_game.begin" then
            view:setVisible(true)
            view:findById("bg_load"):setVisible(false)
            view:findById("lb_load"):setText("Save")
        end
        if event == "save_game.complete" then
            view:setVisible(false)
        end
    end
})