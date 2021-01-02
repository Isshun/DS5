ui:extend({
    type = "view",
    id = "base.ui.right_panel.jobs",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.JobController",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}},
        { type = "label", text = "Jobs", text_color = blue_light_2, text_size = 22, padding = 10, position = {46, 0}},
        { type = "list", id = "list_jobs", position = {10, 40}},
    },
})