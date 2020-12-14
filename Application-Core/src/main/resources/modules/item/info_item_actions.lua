ui:extend({
    type = "view",
    id = "base.ui.panel_item_info.actions",
    parent = "base.ui.panel_item_info.actions_content",
    visible = true,
    views = {

        { type = "view", views = {

            { type = "view", id="building_actions", views = {

                { type = "label", text = "Cancel", text_size = 14, text_color = color2, position = {10, 38}, size = {100, 32}, action = "onCancelBuild" },

            }},

            { type = "view", id="regular_actions", views = {

                { type = "label", text = "Dump", text_size = 14, text_color = color2, position = {10, 8}, size = {100, 32}, action = "onDumpItem" },

            }},

        }},
    },
})
