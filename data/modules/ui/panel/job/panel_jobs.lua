ui:extend({
    type = "view",
    id = "base.ui.right_panel.jobs",
    parent = "base.ui.right_panel.sub_controller",
    controller = "org.smallbox.faraway.client.controller.JobController",
    visible = false,
    views = {
        { type = "list", id = "list_jobs", position = {10, 10}, spacing = 10, template = {
            { type = "view", background = blue_dark_2, size = {370, 60}, views = {
                { type = "label", id = "lb_job", text = "Jobs", text_font = "font3", text_color = 0xffffffe2, text_size = 22, padding = 10, position = {0, 0}},
                { type = "label", id = "lb_status", text = "Jobs", text_font = "sui", text_color = 0xffffffaa, text_size = 14, padding = 10, position = {200, 0}},
                { type = "list", id = "list_status", position = {0, 40}, template = {
                    { type = "view", size = {300, 10}, views = {
                        { type = "label", id = "lb_status_character", text_font = "sui", text_color = 0xffffffaa, text_size = 12, position = {40, 0}},
                        { type = "label", id = "lb_status_label", text_font = "sui", text_color = 0xffffffaa, text_size = 14, position = {200, 0}, text_align = "TOP_RIGHT", size = {160, 10}},
                        { type = "label", id = "lb_status_available", text_font = "sui", text_color = 0xffffffaa, text_size = 12, position = {10, 40}},
                        { type = "view", id = "view_status_available", background = 0xffffffaa, size = {130, 1}, position = {35, 5}},
                        { type = "label", id = "lb_status_index", text_font = "sui", text_color = 0xffffffaa, text_size = 12, position = {10, 0}},
d                    }}
                }},
                { type = "image", id = "img_job", position = {46, 0}},
            }},
        }},
    },
})