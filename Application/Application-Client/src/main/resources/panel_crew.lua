ui:extend({
    type = "view",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    id = "base.ui.panel_crew",
    controller = "org.smallbox.faraway.client.controller.CrewController",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("base.ui.panel_crew"):setVisible(false)
        end},
        { type = "label", text = "Crew", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", id = "list_crew", position = {12, 52}},
    },

--    on_event = function(view, event , data)
--        if event == application.events.on_key_press and data == "ESCAPE" then
--            view:setVisible(false)
--            ui:find("base.ui.panel_main"):setVisible(true)
--            application:sendEvent("mini_map.display", true)
--        end
--    end
})