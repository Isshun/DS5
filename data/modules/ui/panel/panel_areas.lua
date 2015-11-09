data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_areas",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            application.ui:findById("panel_main"):setVisible(true)
            application.ui:findById("panel_areas"):setVisible(false)
        end},
        { type = "label", text = "Areas", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", id = "list_areas", position = {10, 40}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Storage", text_size = 18, padding = 10, on_click = "application:setArea('storage')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Dump", text_size = 18, padding = 10, on_click = "application:setArea('dump')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Home", text_size = 18, padding = 10, on_click = "application:setArea('home')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Sector", text_size = 18, padding = 10, on_click = "application:setArea('sector')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Garden", text_size = 18, padding = 10, on_click = "application:setArea('garden')"},
        }},
        { type = "list", id = "list_areas", position = {205, 40}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Storage", text_size = 18, padding = 10, on_click = "application:removeArea('storage')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Dump", text_size = 18, padding = 10, on_click = "application:removeArea('dump')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Home", text_size = 18, padding = 10, on_click = "application:removeArea('home')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Sector", text_size = 18, padding = 10, on_click = "application:removeArea('sector')"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Garden", text_size = 18, padding = 10, on_click = "application:removeArea('garden')"},
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