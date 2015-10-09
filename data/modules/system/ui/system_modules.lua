game.data:extend(
    {
        {
            type = "view",
            name = "system-ui-modules",
            position = {400, 100},
            size = {400, 800},
            background = 0x2b3036,
            visible = false,
            views = {
                type = "list",
                views = {
                    { type = "label", text = "Modules", text_size = 16, size = {400, 24}, padding = 5, background = 0x55000000 },
                    --{ type = "label", text = "[F9]", text_size = 16, size = {50, 24}, position = {350, 0}, padding = 5 },
                    {
                        type = "list",
                        adapter =
                        {
                            data = game.modules,
                            on_bind = function(view, module)
                                view:setText(module:getInfo().name)
                                view:setTextColor(module:isActivate() and 0x00ff00 or 0xff0000)
                                view:setOnClickListener(function(subview)
                                    module:setActivate(not module:isActivate())
                                    view:setTextColor(module:isActivate() and 0x00ff00 or 0xff0000)
                                end)
                                --view:findById("lb_status"):setText(job:getCharacter() and job:getCharacter():getName() or "idle")
                                --view:findById("view_progress"):setSize(job:getProgress() * 400, 22)
                            end,
                            view = { type = "label", text_size = 14, size = {400, 18}, padding = 5},
                        },
                    },
                    {
                        type = "list",
                        adapter =
                        {
                            data = game.luaModules,
                            on_bind = function(view, module)
                                view:setText(module:getInfo().name)
                                view:setTextColor(module:isActivate() and 0x00ff00 or 0xff0000)
                                view:setOnClickListener(function(subview)
                                    module:setActivate(not module:isActivate())
                                    view:setTextColor(module:isActivate() and 0x00ff00 or 0xff0000)
                                end)
                                --view:findById("lb_status"):setText(job:getCharacter() and job:getCharacter():getName() or "idle")
                                --view:findById("view_progress"):setSize(job:getProgress() * 400, 22)
                            end,
                            view = { type = "label", text_size = 14, size = {400, 18}, padding = 5},
                        },
                    },
                },
            },

            on_load =
            function(view)
                speed = 1
            end,

            on_event =
            function(event, view, data)
                if event == game.events.on_key_press and data == "F9" then
                    view:setVisible(not view:isVisible())
                end
            end,

            on_refresh =
            function(view)
            end
        },
    }
)