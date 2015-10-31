data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_plan",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            game.ui:findById("panel_main"):setVisible(true)
            game.ui:findById("panel_plan"):setVisible(false)
        end},
        { type = "label", text = "Plan", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", position = {0, 40}, views = {
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Mine", text_size = 18, padding = 10, on_click = "game:setPlan('mine')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Gather", text_size = 18, padding = 10, on_click = "game:setPlan('gather')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Cut", text_size = 18, padding = 10, on_click = "game:setPlan('cut')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Destroy", text_size = 18, padding = 10, on_click = "game:setPlan('destroy')"},
        }},
    },
    on_event = function(event, view, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:findById("panel_main"):setVisible(true)
        end
    end
})