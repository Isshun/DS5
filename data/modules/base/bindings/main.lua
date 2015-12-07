data:extend({
    {type = "binding", name = "base.binding.open_panel_main", label = "Open main panel",
        on_check = function()
            if ui:isVisible("base.ui.panel_build") then return true end
            return false
        end,
        on_action = function()
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("base.ui.panel_build"):setVisible(false)
            ui:find("base.ui.panel_areas"):setVisible(false)
            ui:find("base.ui.panel_crew"):setVisible(false)
            ui:find("base.ui.panel_jobs"):setVisible(false)
        end},

    {type = "binding", name = "base.binding.open_panel_build", label = "Open build panel",
        on_check = function() return ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            ui:find("base.ui.panel_main"):setVisible(false)
            ui:find("base.ui.panel_build"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.open_panel_plan", label = "Open plan panel",
        on_check = function() return ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            ui:find("base.ui.panel_main"):setVisible(false)
            ui:find("base.ui.panel_plan"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.open_panel_jobs", label = "Open jobs panel",
        on_check = function() return ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            ui:find("base.ui.panel_main"):setVisible(false)
            ui:find("base.ui.panel_jobs"):setVisible(true)
        end},

    {type = "binding", name = "base.binding.open_panel_crew", label = "Open crew panel",
        on_check = function() return ui:isVisible("base.ui.panel_main") end,
        on_action = function()
            ui:find("base.ui.panel_main"):setVisible(false)
            ui:find("base.ui.panel_crew"):setVisible(true)
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
    {type = "binding", name = "base.binding.toggle_game_pause", label = "toggle game pause",
        on_check = function() return application.game end,
        on_action = function() application.game:toggleRunning() end},
    {type = "binding", name = "base.binding.toggle_speed_0", label = "Suspend/resume time",
        on_check = function() return application.game end,
        on_action = function() application.game:toggleSpeed0() end},
    {type = "binding", name = "base.binding.set_speed_1", label = "Set speed 1",
        on_check = function() return application.game end,
        on_action = function() application.game:setSpeed(1) end},
    {type = "binding", name = "base.binding.set_speed_2", label = "Set speed 2",
        on_check = function() return application.game end,
        on_action = function() application.game:setSpeed(2) end},
    {type = "binding", name = "base.binding.set_speed_3", label = "Set speed 3",
        on_check = function() return application.game end,
        on_action = function() application.game:setSpeed(3) end},
    {type = "binding", name = "base.binding.set_speed_4", label = "Set speed 4",
        on_check = function() return application.game end,
        on_action = function() application.game:setSpeed(4) end},
})