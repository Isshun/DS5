game.data:extend(
{
    {
        type = "view",
        position = {1200, 65},
        size = {400, 800},
        background = 0x121c1e,
        id = "panel_jobs",
        visible = false,
        views = {
            { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
                game.ui:findById("panel_main"):setVisible(true)
                game.ui:findById("panel_jobs"):setVisible(false)
            end},
            { type = "label", text = "Jobs", text_size = 28, padding = 10, position = {40, 0}},
            { type = "list", id = "list_jobs", position = {10, 40}, views = {
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "Mine", text_size = 18, padding = 10, on_click = "game:setPlan('mining')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "Gather", text_size = 18, padding = 10, on_click = "game:setPlan('gather')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "Cut", text_size = 18, padding = 10, on_click = "game:setPlan('cut')"},
            }},
        },
        on_refresh = function(view)
            list = view:findById("list_jobs")
            list:removeAllViews()

            local iterator = game.jobs:getJobs():iterator()
            while (iterator:hasNext()) do
                local job = iterator:next();
                job_entry = game.ui:createLabel()
                job_entry:setSize(400, 22)
                job_entry:setText(job:getLabel() .. job:getProgressPercent() .. (job:getCharacter() and job:getCharacter():getName() or ""))
                job_entry:setTextSize(18)
                --job_entry:setBackgroundColor(0x885577)
                job_entry:setOnClickListener(function(view)
                    --openCategory(key, value)
                end)
                list:addView(job_entry)
            end

        end,
    },
}
)