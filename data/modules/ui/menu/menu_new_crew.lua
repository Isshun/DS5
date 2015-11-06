data:extend({
    type = "view",
    id = "base.ui.menu_new_crew",
    size = {300, -1},
    background = 0xdd121c1e,
    in_game = false,
    visible = false,
    views = {
        { type = "image", id = "img_background", size = {1920, 1200}},
        { type = "label", text = "Crew", text_size = 22, position = {500, 350}},
        { type = "list", id = "list_crew", position = {500, 400}},
        { type = "label", text = "next", text_size = 22, background = 0x121c1e, size = {100, 40}, position = {1000, 800}, on_click = function()
            application.ui:findById("base.ui.menu_new_crew"):setVisible(false)
            application:sendEvent("new_game.start")
        end},
    },

    on_load = function(view)
    end,

    on_event = function(view, event, data)
    end,

})