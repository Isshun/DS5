ui:extend({
    type = "view",
    id = "base.ui.right_panel.jobs",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.JobController",
    visible = false,
    views = {
        { type = "list", id = "list_jobs", position = {10, 10}, spacing = 10, template = {
            { type = "view", background = blue_dark_3, size = {370, 55}, views = {
                { type = "label", id = "lb_job", text = "Jobs", text_font = "font3", text_color = 0xe62317cc, text_size = 22, padding = 10, position = {0, 0}},
                { type = "label", id = "lb_character", text = "Jobs", text_font = "sui", text_color = 0xe62317cc, text_size = 10, padding = 10, position = {0, 28}},
                { type = "image", id = "img_job", position = {46, 0}},
            }},
        }},
    },
})