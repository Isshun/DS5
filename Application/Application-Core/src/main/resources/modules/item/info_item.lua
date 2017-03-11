local item

ui:extend({
    type = "view",
    name = "base.ui.panel_item_info",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.ItemInfoController",
    visible = false,
    views = {

        { type = "view", position = {275, 15}, size = {80, 25}, background = 0x3e4b0b, views = {
            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00 },
            { type = "label", id = "lb_health", text_color = color2, text_size = 16, padding = 7 },
        }},

        { type = "list", views = {

            { type = "label", text = "Item", text_color = color1, text_size = 12, margin = {12, 12, 0, 12}},
            { type = "label", id = "lb_name", text = "name", text_size = 28, text_color = color2, margin = {12, 12} },

            { type = "label", text = "Dump", text_size = 12, position = {10, 8}, size = {100, 32}, action = "onDump"},

            -- Detailled informations
            { type = "list", id = "frame_content", position = {0, 60}, views = {

                -- Building
                { type = "list", id = "frame_build", position = {0, 60}, margin = {10, 0, 0, 10}, size = {300, 200}, views = {
                    { type = "label", id = "lb_build_progress", text_size = 22, size = {400, 26}},
                    { type = "image", id = "img_build_progress", src = "[base]/graphics/needbar.png", size = {380, 16}, texture_rect = {0, 0, 100, 16}},
                    { type = "label", id = "lb_build_cost", text_size = 12, size = {400, 26}},

                    { type = "list", id = "frame_components", size = {300, 100}, views = {
                        { type = "label", text = "Components", text_size = 20, margin = {10, 0, 0, 0}},
                        { type = "list", id = "list_components", position = {0, 10}},
                    }},
                }},

                -- Workers
                { type = "list", id = "frame_workers", margin = {10, 0, 0, 10}, size = {300, 200}, views = {
                    { type = "label", text = "Workers", text_size = 20},
                    { type = "list", id = "list_workers"},
                }},

                -- Effects
                { type = "label", id = "lb_effect_oxygen", text_size = 18, padding = 10},

                -- Slots
                { type = "label", id = "lb_slots", text_size = 18, padding = 10},
                { type = "label", id = "lb_used_by", text_size = 18, padding = 10},

                -- Actions
                { type = "list", id = "frame_actions", margin = {10, 0, 0, 10}, views = {
                    { type = "label", text = "Actions", text_color = color1, text_size = 22, size = {400, 26}},
                    { type = "label", id = "current_action", text_color = color2, size = {400, 20}},
                    { type = "list", id = "list_actions"},
                }},

                -- Factory progress
                { type = "list", id = "frame_factory", position = {0, 300}, margin = {40, 0, 0, 10}, views = {
                    { type = "label", text = "Factory", text_size = 22},
                    { type = "label", id = "lb_factory_message", size = {100, 20}},
                    { type = "label", id = "lb_factory_job", size = {100, 20}},
                    --                { type = "image", id = "img_factory_progress", src = "[base]/graphics/needbar.png", size = {352, 16}},

                    -- Factory inventory
                    { type = "list", id = "frame_factory_inventory", margin = {10, 0, 0, 10}, views = {
                        { type = "label", text = "Inventory", text_size = 18, size = {400, 26}},
                        { type = "list", id = "list_factory_inventory"},
                    }},
                }},

                -- Action buttons
                { type = "label", id = "bt_dump", text = "Dump", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 10, position = {10, 380}, size = {350, 32}, on_click = function()
                    application:destroy(item)
                end},
                { type = "label", id = "bt_cancel", text = "Cancel", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 10, position = {10, 380}, size = {350, 32}, on_click = function()
                    application:cancel(item)
                end},
            }},
        }},
    },
})
