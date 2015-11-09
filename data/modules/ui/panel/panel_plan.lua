data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_plan",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            application.ui:findById("panel_main"):setVisible(true)
            application.ui:findById("panel_plan"):setVisible(false)
        end},
        { type = "label", text = "Plan", text_size = 28, padding = 10, position = {40, 0}},
        { type = "grid", columns = 1, row_height = 50, position = {10, 50}, views = {
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Dig", text_size = 18, padding = 10, on_click = "application:setPlan('dig')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Dig ramp up", text_size = 18, padding = 10, on_click = "application:setPlan('dig_ramp_up')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Dig ramp down", text_size = 18, padding = 10, on_click = "application:setPlan('dig_ramp_down')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Dig hole", text_size = 18, padding = 10, on_click = "application:setPlan('dig_hole')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Harverst", text_size = 18, padding = 10, on_click = "application:setPlan('gather')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Cut down", text_size = 18, padding = 10, on_click = "application:setPlan('cut')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Dump", text_size = 18, padding = 10, on_click = "application:setPlan('destroy')"},
        }},
    },
    on_event = function(view, event , data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.ui:findById("panel_main"):setVisible(true)
        end
    end
})