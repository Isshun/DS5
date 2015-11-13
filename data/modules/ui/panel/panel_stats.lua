data:extend({
    type = "view",
    position = {application.info.screen_width - 400, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_stats",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            application.ui:findById("panel_main"):setVisible(true)
            application.ui:findById("panel_stats"):setVisible(false)
        end},
        { type = "label", text = "Stats", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", position = {0, 40}, views = {
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Mine", text_size = 18, padding = 10, on_click = "application:setPlan('mining')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Gather", text_size = 18, padding = 10, on_click = "application:setPlan('gather')"},
            { type = "label", size = {180, 40}, background = 0x8b9076, text = "Cut", text_size = 18, padding = 10, on_click = "application:setPlan('cut')"},
        }},
    },
    on_event = function(view, event , data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.ui:findById("panel_main"):setVisible(true)
            application:sendEvent("mini_map.display", true)
        end
    end
})