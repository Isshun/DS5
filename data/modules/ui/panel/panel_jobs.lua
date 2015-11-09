data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_jobs",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}, on_click = function()
            application.ui:findById("panel_main"):setVisible(true)
            application.ui:findById("panel_jobs"):setVisible(false)
        end},
        { type = "label", text = "Jobs", text_size = 28, padding = 10, position = {46, 0}},
        { type = "list", id = "list_jobs", position = {10, 40}},
    },
    on_event = function(view, event , data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.ui:findById("panel_main"):setVisible(true)
            application:sendEvent("mini_map.display", true)
        end
    end,

    on_refresh = function(view)
        local list = view:findById("list_jobs")
        list:removeAllViews()

        local iterator = application.jobs:iterator()
        while iterator:hasNext() do
            local job = iterator:next()
            if not job:isFinish() then
                local frame_job = application.ui:createView()
                frame_job:setSize(400, 22)

                local lb_job = application.ui:createLabel()
                --            lb_job:setText(job:getLabel())
                lb_job:setSize(400, 20)
                lb_job:setDashedString(job:getLabel(), job:getStatus():toString(), 47)
                lb_job:setOnClickListener(function()
                    print (job:getMessage())
                end)
                frame_job:addView(lb_job)

                --            local lb_job_message = application.ui:createLabel()
                --            lb_job_message:setText(job:getMessage())
                --            lb_job_message:setPosition(200, 0)
                --            frame_job:addView(lb_job_message)

                list:addView(frame_job)
            end
        end
    end
})