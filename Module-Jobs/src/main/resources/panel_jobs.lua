data:extend({
    type = "view",
    style = "base.style.right_panel",
    id = "base.ui.panel_jobs",
    controller = "org.smallbox.faraway.module.job.JobController",
    group = "right_panel",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}, on_click = function()
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("base.ui.panel_jobs"):setVisible(false)
        end},
        { type = "label", text = "Jobs", text_size = 28, padding = 10, position = {46, 0}},
        { type = "list", id = "list_jobs", position = {10, 40}},
    },
})