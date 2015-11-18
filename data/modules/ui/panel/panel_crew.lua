data:extend({
    type = "view",
    style = "base.style.right_panel",
    id = "base.ui.panel_crew",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            application.ui:findById("base.ui.panel_main"):setVisible(true)
            application.ui:findById("base.ui.panel_crew"):setVisible(false)
        end},
        { type = "label", text = "Crew", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", position = {0, 40}, adapter = {
            view = { type = "label", id = "lb_character", size = {180, 40}, text = "hello", text_size = 18, padding = 10},
            data = application.crew:list(),
            on_bind = function(view, data)
                view:setText(data.name)
            end
        }},
    },
    on_event = function(view, event , data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.ui:findById("base.ui.panel_main"):setVisible(true)
            application:sendEvent("mini_map.display", true)
        end
    end
})