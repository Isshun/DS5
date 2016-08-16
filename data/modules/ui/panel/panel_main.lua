local parcel

data:extend({
    type = "view",
    id = "base.ui.panel_main",
    style = "base.style.right_panel",
    controller = "org.smallbox.faraway.module.mainPanel.MainPanelController",
    group = "right_panel",
    visible = true,
    layer = -1,
    views = {{
        type = "list",
        position = {10, 0},
        views = {
            { type = "view", position = {0, 12}, size = {352, 34}, background = 0x203636, views = {
                { type = "label", text = "Far Point", text_size = 16, padding = 10 },
                { type = "label", text = "Arrakis / Desert", text_size = 16, position = {160, 0}, padding = 10 },
                { type = "label", id = "lb_floor", text = "[0]", text_color = 0xffffff, text_size = 16, position = {312, 0}, padding = 10 },
            }},
            { type = "view", size = {352, 230}, views = {
                { type = "minimap"},
                { type = "image", id = "img_speed", position = {316, 205}, size = {32, 32}},
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
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Build", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_build") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Plan", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_plan") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Displays", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_displays") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Areas", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_areas") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Jobs", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_jobs") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Crew", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_crew") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Stats", text_size = 18, padding = 10, on_click = function() open_panel("base.ui.panel_stats") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Networks", text_size = 18, padding = 10, on_click = function() open_panel("panel_networks") end},
--                { type = "label", size = {170, 40}, background = {regular = 0x349394, focus = 0x25c9cb}, text = "Debug", text_size = 18, padding = 10, on_click = function() open_panel("panel_debug") end, id = "lb_debug"},
            }},
        }},
        {
            type = "view",
            position = {0, application.info.screen_height - 150},
            views = {
                { type = "label", id = "lb_ground", text_size = 14},
                { type = "grid", columns = 10, column_width = 58, row_height = 100, position = {10, 10}, views = {
                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                        { type = "image", id = "thumb_o2", src = "[base]/graphics/icons/thumb_o2.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                        { type = "label", id = "lb_oxygen", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
                    }},

                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                        { type = "image", id = "thumb_walkable", src = "[base]/graphics/icons/thumb_walkable.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                        { type = "label", id = "lb_walkable", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
                    }},

                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                        { type = "image", id = "thumb_water", src = "[base]/graphics/icons/thumb_water.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                        { type = "label", id = "lb_water", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
                    }},

                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                        { type = "image", id = "thumb_temperature", src = "[base]/graphics/icons/thumb_temperature.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                        { type = "label", id = "lb_temperature", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
                    }},

                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                        { type = "image", id = "thumb_temperature", src = "[base]/graphics/icons/thumb_light.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                        { type = "label", id = "lb_light", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
                    }},

                    { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                        { type = "image", id = "thumb_inside", src = "[base]/graphics/icons/thumb_home.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                        { type = "label", id = "lb_inside", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
                    }},
                }}
            }}
    },

    on_game_start = function(view)
        if application.game then
            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. application.game:getSpeed() .. ".png");
        end
    end,

--    on_event = function(view, event, data)
--        view:setVisible(true)
--
--        -- Check bindings
--        if event == application.events.on_binding then
--            if data == application.bindings.open_base.ui.panel_build then open_panel("base.ui.panel_build") end
--            if data == application.bindings.open_panel_plan then open_panel("panel_plan") end
--            if data == application.bindings.open_panel_tasks then open_panel("base.ui.panel_jobs") end
--            if data == application.bindings.open_base.ui.panel_crew then open_panel("base.ui.panel_crew") end
--            if data == application.bindings.open_base.ui.panel_displays then open_panel("base.ui.panel_displays") end
--            if data == application.bindings.open_base.ui.panel_stats then open_panel("base.ui.panel_stats") end
--            if data == application.bindings.open_base.ui.panel_areas then open_panel("base.ui.panel_areas") end
--            if data == application.bindings.open_panel_debug then open_panel("base.ui.panel_debug") end
--        end
--
--        if event == application.events.on_parcel_over then
--            parcel = data;
--        end
--
--        if event == application.events.on_deselect then
--            view:setVisible(true)
--        end
--
--        if event == application.events.on_floor_change then
--            view:findById("lb_floor"):setText("[" .. data .. "]");
--        end
--
--        if event == application.events.on_speed_change then
--            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. data .. ".png");
--        end
--
--        if event == application.events.on_game_paused then
--            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_0.png");
--        end
--
--        if event == application.events.on_speed_resume then
--            view:findById("img_speed"):setImage("[base]/graphics/ic_speed_" .. application.game:getSpeed() .. ".png");
--        end
--
--        if event == application.events.on_display_change then
--            local lb_display = view:findById("lb_display_" .. data[1])
--            if lb_display then
--                lb_display:setTextColor(data[2] and 0x48e3e4 or 0xffffff)
--            end
--        end
--
--        if event == application.events.on_display_change and data[1] == "debug" then
--            view:findById("lb_debug"):setVisible(data[2])
--        end
--
--        if event == application.events.on_resource_selected
--                or event == application.events.on_character_selected
--                --or event == application.events.on_parcel_selected
--                or event == application.events.on_area_selected
--                or event == application.events.on_structure_selected
--                or event == application.events.on_item_selected
--                or event == application.events.on_consumable_selected then
--            --view:setVisible(false)
--        end
--    end,

--    on_refresh = function(view)
--        view:setVisible(true)
--
--        if parcel ~= nil then
--            local room = parcel:getRoom()
--            view:findById("lb_ground"):setText("Ground", ": ", parcel:getGroundInfo() and parcel:getGroundInfo().name or "no")
--
--            view:findById("lb_light"):setText(parcel:getLight())
--
--            local oxygen = math.round(parcel:getOxygen() * 100)
--            view:findById("lb_oxygen"):setPadding(0, 0, 0, oxygen < 100 and 8 or 0)
--            view:findById("lb_oxygen"):setText(oxygen < 0 and "NA" or (oxygen .. "%"))
--            view:findById("lb_oxygen"):setTextColor(oxygen < 50 and 0xfe5555 or 0xb3d035)
--            view:findById("thumb_o2"):setBackgroundColor(oxygen < 50 and 0xfe5555 or 0xb3d035)
--
--            view:findById("lb_water"):setText("free")
--            view:findById("lb_temperature"):setText(math.floor(parcel:getTemperature()) .. "°")
--
--            --            view:findById("lb_room"):setText("Room", ": ", (room and (room:isExterior() and "exterior" or room:getType():name()) or "no"))
--
--            view:findById("lb_walkable"):setText((parcel:isWalkable() and "yes" or "no"))
--
--            view:findById("thumb_inside"):setBackgroundColor(room and room:isExterior() and 0xbbbbbb or 0xb3d035)
--            view:findById("lb_inside"):setTextColor(room and room:isExterior() and 0xbbbbbb or 0xb3d035)
--            view:findById("lb_inside"):setText((room and (room:isExterior() and "exterior" or room:getType():name()) or "inside"))
--        end
--    end
})
--
--function open_panel(panel_name)
--    application:sendEvent("mini_map.display", false)
--    ui:find("base.ui.panel_main"):setVisible(false)
--    ui:find(panel_name):setVisible(true)
--end