ui:extend(
    {
        {
            type = "view",
            id = "system-ui-modules",
            position = {400, 100},
            size = {400, 800},
            background = 0x2b3036ff,
            visible = false,
            views = {
                type = "list",
                views = {
                    { type = "label", text = "Modules", text_size = 16, size = {400, 24}, padding = 5, background = 0x00000055 },
                    { type = "view", size = {400, 32}, views = {
                        { type = "label", text = "Base", text_size = 16, size = {150, 24}, position = {0, 0}, padding = 5, background = 0x00000055, on_click = function()
                            ui:find("system-ui-modules"):findById("list_modules"):getAdapter():setData(application.modules)
                        end},
                        { type = "label", text = "Third", text_size = 16, size = {150, 24}, position = {150, 0}, padding = 5, background = 0x00000055, on_click = function()
                            ui:find("system-ui-modules"):findById("list_modules"):getAdapter():setData(application.moduleThirds)
                        end},
                        { type = "label", text = "Lua", text_size = 16, size = {150, 24}, position = {300, 0}, padding = 5, background = 0x00000055, on_click = function()
                            ui:find("system-ui-modules"):findById("list_modules"):getAdapter():setData(application.luaModules)
                        end},
                    }},
                    --{ type = "label", text = "[F9]", text_size = 16, size = {50, 24}, position = {350, 0}, padding = 5 },
                    {
                        type = "list",
                        id = "list_modules",
                        adapter =
                        {
                            data = application.modules,
                            on_bind = function(view, module)
                                local prefix = "[B]"
                                if module:getInfo().type == "java" then prefix = "[T]" end
                                if module:getInfo().type == "lua" then prefix = "[L]" end
                                view:setText(prefix .. " " .. module:getInfo().name .. " (" .. module:getModuleUpdateTime() .. ")")
                                view:setTextColor(module:isLoaded() and 0x00ff00ff or 0xff0000ff)
                                view:setOnClickListener(function(subview)
                                    application.events:send("module.open_detail", module:getInfo().name)
--                                    module:setActivate(not module:isActivate())
--                                    view:setTextColor(module:isActivate() and 0x00ff00ff or 0xff0000ff)
                                end)
                                --view:findById("lb_status"):setText(job:getCharacter() and job:getCharacter():getName() or "idle")
                                --view:findById("view_progress"):setSize(job:getProgress() * 400, 22)
                            end,
                            view = { type = "label", text_size = 14, size = {400, 18}, padding = 5},
                        },
                    },
                },
            },

            on_game_start =
            function(view)
                speed = 1
            end,

            on_event =
            function(view, event, data)
                if event == application.events.on_key_press and data == "F9" then
                    view:setVisible(not view:isVisible())
                end
            end,

            on_refresh =
            function(view)
            end
        },
    }
)