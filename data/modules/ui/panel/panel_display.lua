data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_displays",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}, on_click = function()
            game.ui:findById("panel_main"):setVisible(true)
            game.ui:findById("panel_displays"):setVisible(false)
        end},
        { type = "label", text = "Displays", text_size = 28, padding = 10, position = {46, 0}},
        { type = "list", id = "list_displays", position = {10, 40}, views = {
            { type = "label", text = "Regular", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "regular") end},
            { type = "label", text = "Areas", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "areas") end},
            { type = "label", text = "Rooms", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "rooms") end},
            { type = "label", text = "Temperature", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "temperature") end},
            { type = "label", text = "Oxygen", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "oxygen") end},
            { type = "label", text = "Security", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "security") end},
            { type = "label", text = "Water", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "water") end},
            { type = "label", text = "Electricity", text_size = 18, size = {400, 32}, padding = 10, on_click = function(v) setDisplay(v, "electricity") end},
        }},
    },
    on_event = function(view, event , data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:findById("panel_main"):setVisible(true)
        end
    end,
})

function setDisplay(view, display)
    local iterator = view:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x121c1e)
    end
    view:setBackgroundColor(0xff0000)
    game:setDisplay(display)
end