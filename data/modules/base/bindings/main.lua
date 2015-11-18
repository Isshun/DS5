data:extend({
    {type = "binding", name = "base.binding.open_panel_main", label = "Open main panel",
        on_check = function()
            if application.ui:isVisible("base.ui.panel_build") then return true end
            return false
        end,
        on_action = function()
            application.ui:findById("base.ui.panel_main"):setVisible(true)
            application.ui:findById("base.ui.panel_build"):setVisible(false)
            application.ui:findById("base.ui.panel_areas"):setVisible(false)
            application.ui:findById("base.ui.panel_tasks"):setVisible(false)
            application.ui:findById("base.ui.panel_crew"):setVisible(false)
            application.ui:findById("base.ui.panel_jobs"):setVisible(false)
        end},

    {type = "binding", name = "base.binding.open_panel_build", label = "Open build panel",
        on_check = function() return application.ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            application.ui:findById("base.ui.panel_main"):setVisible(false)
            application.ui:findById("base.ui.panel_build"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.open_panel_plan", label = "Open plan panel",
        on_check = function() return application.ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            application.ui:findById("base.ui.panel_main"):setVisible(false)
            application.ui:findById("base.ui.panel_plan"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.open_panel_jobs", label = "Open jobs panel",
        on_check = function() return application.ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            application.ui:findById("base.ui.panel_main"):setVisible(false)
            application.ui:findById("base.ui.panel_jobs"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.open_panel_crew", label = "Open crew panel",
        on_check = function() return application.ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            application.ui:findById("base.ui.panel_main"):setVisible(false)
            application.ui:findById("base.ui.panel_crew"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.toggle_display_areas", label = "toggle areas display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("areas") end},

    {type = "binding", name = "base.binding.toggle_display_rooms", label = "toggle rooms display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("rooms") end},

    {type = "binding", name = "base.binding.toggle_display_temperature", label = "toggle temperature display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("temperature") end},

    {type = "binding", name = "base.binding.toggle_display_oxygen", label = "toggle oxygen display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("oxygen") end},

    {type = "binding", name = "base.binding.toggle_display_water", label = "toggle water display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("water") end},

    {type = "binding", name = "base.binding.toggle_display_security", label = "toggle security display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("security") end},

    {type = "binding", name = "base.binding.toggle_display_debug", label = "toggle debug display",
        on_check = function() return true end,
        on_action = function() application:toggleDisplay("debug") end},
})