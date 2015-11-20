data:extend({
    type = "view",
    id = "base.ui.panel_main",
    style = "base.style.right_panel",
    visible = true,
    views = {
        type = "list",
        position = {10, 0},
        views = {
            { type = "view", position = {0, 12}, size = {352, 34}, background = 0x203636, views = {
                { type = "label", text = "Far Point", text_size = 16, padding = 10 },
                { type = "label", text = "Arrakis / Desert", text_size = 16, position = {190, 0}, padding = 10 },
            }},
            { type = "view", size = {352, 230}, views = {
--                { type = "image", id = "img_map", background = 0x000000, size = {380, 240}},
                { type = "label", id = "lb_floor", text = "0", text_size = 22, position = {12, 205}},
            }},
            { type = "grid", columns = 20, column_width = 30, row_height = 38, background = 0x203636, size = {352, 34}, views = {
                { type = "label", text = "A", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_areas", on_click = function(v) application:toggleDisplay("areas") end},
                { type = "label", text = "R", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_rooms", on_click = function(v) application:toggleDisplay("rooms") end},
                { type = "label", text = "T", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_temperature", on_click = function(v) application:toggleDisplay("temperature") end},
                { type = "label", text = "O", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_oxygen", on_click = function(v) application:toggleDisplay("oxygen") end},
                { type = "label", text = "W", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_water", on_click = function(v) application:toggleDisplay("water") end},
                { type = "label", text = "S", text_size = 16, padding = 7, position = {4, 6}, size = {24, 24}, background = {regular = 0x203636, focus = 0x203636}, id = "lb_display_security", on_click = function(v) application:toggleDisplay("security") end},
            }},
            { type = "grid", id = "main_grid", position = {0, 32}, columns = 2, column_width = 180, row_height = 50, focusable = true, views = {
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Build", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_build") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Plan", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_plan") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Displays", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_displays") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Areas", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_areas") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Jobs", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_jobs") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Crew", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_crew") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Stats", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_stats") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Networks", text_size = 18, padding = 10, on_click = function() open_panel("panel_networks") end},
                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Debug", text_size = 18, padding = 10, on_click = function() open_panel("panel_debug") end, id = "lb_debug"},
            }}
        },
    },

    on_event = function(view, event , data)

        -- Check bindings
        if event == application.events.on_binding then
            if data == application.bindings.open_base.ui.panel_build then open_panel("base.ui.panel_build") end
            if data == application.bindings.open_panel_plan then open_panel("panel_plan") end
            if data == application.bindings.open_panel_tasks then open_panel("base.ui.panel_jobs") end
            if data == application.bindings.open_base.ui.panel_crew then open_panel("base.ui.panel_crew") end
            if data == application.bindings.open_base.ui.panel_displays then open_panel("base.ui.panel_displays") end
            if data == application.bindings.open_base.ui.panel_stats then open_panel("base.ui.panel_stats") end
            if data == application.bindings.open_base.ui.panel_areas then open_panel("base.ui.panel_areas") end
            if data == application.bindings.open_panel_debug then open_panel("panel_debug") end
        end

        if event == application.events.on_deselect then
            view:setVisible(true)
        end

        if event == application.events.on_floor_change then
            view:findById("lb_floor"):setText(data);
        end

        if event == application.events.on_display_change then
            local lb_display = view:findById("lb_display_" .. data[1])
            if lb_display then
                lb_display:setTextColor(data[2] and 0x48e3e4 or 0xffffff)
            end
        end

        if event == application.events.on_display_change and data[1] == "debug" then
            view:findById("lb_debug"):setVisible(data[2])
        end

        if event == application.events.on_resource_selected
                or event == application.events.on_character_selected
                --or event == application.events.on_parcel_selected
                or event == application.events.on_area_selected
                or event == application.events.on_structure_selected
                or event == application.events.on_item_selected
                or event == application.events.on_consumable_selected then
            view:setVisible(false)
        end
    end,
})

function open_panel(panel_name)
    application:sendEvent("mini_map.display", false)
    application.ui:findById("base.ui.panel_main"):setVisible(false)
    application.ui:findById(panel_name):setVisible(true)
end