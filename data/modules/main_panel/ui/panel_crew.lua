data:extend({
    type = "view",
    position = {1200, 65},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_crew",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            game.ui:findById("panel_main"):setVisible(true)
            game.ui:findById("panel_crew"):setVisible(false)
        end},
        { type = "label", text = "Crew", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", position = {0, 40}, adapter = {
            view = { type = "label", id = "lb_character", size = {180, 40}, text = "hello", text_size = 18, padding = 10},
            data = game.crew:list(),
            on_bind = function(view, data)
                view:setText(data.name)
            end
        }},
    },
    on_event = function(event, view, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:findById("panel_main"):setVisible(true)
        end
    end
})