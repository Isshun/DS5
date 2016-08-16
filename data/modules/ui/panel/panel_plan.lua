data:extend({
    type = "view",
    style = "base.style.right_panel",
    id = "base.ui.panel_plan",
    visible = false,
    group = "right_panel",
    on_click = function() close_sub_menu() end,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("panel_plan"):setVisible(false)
        end},
        { type = "label", text = "Plan", text_size = 28, padding = 10, position = {40, 0}},
        { type = "grid", id = "main_menu", columns = 1, row_height = 50, position = {10, 50}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dig", text_size = 18, padding = 10, on_click = function() open_sub_menu("sub_menu_dig") end},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Harverst", text_size = 18, padding = 10, on_click = function() open_sub_menu("sub_menu_gather") end},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dump", text_size = 18, padding = 10, on_click = function() open_sub_menu("sub_menu_dump") end},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Cancel", text_size = 18, padding = 10, on_click = function() open_sub_menu("sub_menu_cancel") end},
        }},
        { type = "grid", id = "sub_menu_dig", visible = false, columns = 1, row_height = 50, position = {200, 50}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dig", text_size = 18, padding = 10, on_click = "application:setPlan('dig')"},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dig ramp up", text_size = 18, padding = 10, on_click = "application:setPlan('dig_ramp_up')"},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dig ramp down", text_size = 18, padding = 10, on_click = "application:setPlan('dig_ramp_down')"},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dig hole", text_size = 18, padding = 10, on_click = "application:setPlan('dig_hole')"},
        }},
        { type = "grid", id = "sub_menu_gather", visible = false, columns = 1, row_height = 50, position = {200, 50}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Harvest", text_size = 18, padding = 10, on_click = "application:setPlan('gather')"},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Harvest and cut", text_size = 18, padding = 10, on_click = "application:setPlan('cut')"},
        }},
        { type = "grid", id = "sub_menu_dump", visible = false, columns = 1, row_height = 50, position = {200, 50}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dump everything", text_size = 18, padding = 10, on_click = "application:setPlan('destroy')"},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dump structures", text_size = 18, padding = 10, on_click = "application:setPlan('destroy')"},
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Dump objects", text_size = 18, padding = 10, on_click = "application:setPlan('destroy')"},
        }},
        { type = "grid", id = "sub_menu_cancel", visible = false, columns = 1, row_height = 50, position = {200, 50}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Cancel all jobs", text_size = 18, padding = 10, on_click = "application:setPlan('cancel')"},
        }},
    },
    on_event = function(view, event , data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            ui:find("base.ui.panel_main"):setVisible(true)
            application:sendEvent("mini_map.display", true)
        end
    end
})

function open_sub_menu(id)
    close_sub_menu()
    local iterator = ui:find("base.ui.panel_plan"):findById("main_menu"):getViews():iterator()
    while iterator:hasNext() do
        local menu_entry = iterator:next()
        menu_entry:setBackgroundColor(0x34939455)
        menu_entry:setRegularBackgroundColor(0x34939455)
        menu_entry:setFocusBackgroundColor(0x349394bb)
    end
--    ui:find("base.ui.panel_plan"):findById("main_menu"):setActive(false)
    ui:find("base.ui.panel_plan"):findById(id):setVisible(true)
end

function close_sub_menu()
    local iterator = ui:find("base.ui.panel_plan"):findById("main_menu"):getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x349394)
    end
--    ui:find("base.ui.panel_plan"):findById("main_menu"):setActive(true)
    ui:find("base.ui.panel_plan"):findById("sub_menu_dig"):setVisible(false)
    ui:find("base.ui.panel_plan"):findById("sub_menu_gather"):setVisible(false)
    ui:find("base.ui.panel_plan"):findById("sub_menu_dump"):setVisible(false)
    ui:find("base.ui.panel_plan"):findById("sub_menu_cancel"):setVisible(false)
end