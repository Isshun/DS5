data:extend(
    {
        {
            type = "view",
            position = {1200, 65},
            size = {400, 800},
            background = 0x121c1e,
            id = "panel_jobs",
            visible = false,
            views = {
                { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}, on_click = function(view)
                    game.ui:findById("panel_main"):setVisible(true)
                    game.ui:findById("panel_jobs"):setVisible(false)
                end},
                { type = "label", text = "Jobs", text_size = 28, padding = 10, position = {46, 0}},
                { type = "list", id = "list_jobs", position = {0, 40}},
--                { type = "list", position = {0, 40}, adapter = {
--                    view = {
--                        type = "view",
--                        size = {400, 22},
--                        views = {
--                            { type = "view", id = "view_progress", background = 0x338855, size = {0, 22}},
--                            { type = "label", id = "lb_job", position = {10, 6}, text_size = 16},
--                            { type = "label", id = "lb_status", position = {200, 6}, text_size = 16, align = "right"},
--                        },
--                    },
--                    data = game.jobs:getJobs(),
--                    on_bind = function(view, job)
--                        if job then
--                            --                            view:findById("lb_job"):setDashedString(job:getLabel(), job:getCharacter() and job:getCharacter():getName() or "idle", 42)
--                            view:findById("lb_job"):setText(job:getLabel())
--                            --                    view:findById("lb_status"):setText(job:getCharacter() and job:getCharacter():getName() or "idle")
--                            view:findById("view_progress"):setSize(job:getProgress() * 400, 22)
--                        end
--                    end
--                }},
            },
            on_event = function(event, view, data)
                if event == game.events.on_key_press and data == "ESCAPE" then
                    view:setVisible(false)
                    game.ui:findById("panel_main"):setVisible(true)
                end
            end,

            on_refresh = function(view)
                local list = view:findById("list_jobs")
                list:removeAllViews()

--                local iterator = game.jobs:getJobs():iterator()
                local iterator = game.jobs:iterator()
                while iterator:hasNext() do
                    local job = iterator:next()
                    local frame_job = game.ui:createView()
                    frame_job:setSize(400, 22)

                    local lb_job = game.ui:createLabel()
                    lb_job:setText(job:getLabel())
                    frame_job:addView(lb_job)

--                    local lb_mode = game.ui:createLabel()
--                    lb_mode:setText("mode")
--                    lb_mode:setSize(50, 22)
--                    lb_mode:setPosition(300, 0)
--                    lb_mode:setOnClickListener(function(v)
--                        lb_mode:setText("gg")
--                    end)
--                    frame_receipt:addView(lb_mode)
--
--                    local lb_active = game.ui:createLabel()
--                    lb_active:setText("[x]")
--                    lb_active:setSize(50, 22)
--                    lb_active:setPosition(358, 0)
--                    lb_active:setOnClickListener(function(v)
--                        lb_active:setText("[ ]")
--                    end)
--                    frame_receipt:addView(lb_active)

                    list:addView(frame_job)
                end
            end
        },
    }
)