consumable = nil

ui:extend({
    type = "view",
    id = "base.ui.info_consumable",
    controller = "org.smallbox.faraway.client.controller.ConsumableInfoController",
    parent = "base.ui.right_panel",
    level = 10,
    visible = false,
    views = {
        { type = "label", id = "lb_label", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "label", text = "Consumable", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "list", position = {10, 70}, views = {
            { type = "label", id = "lb_name", text_size = 16},
            { type = "label", id = "lb_quantity", text_size = 16},
            { type = "label", id = "lb_job", text_size = 16},
            { type = "label", id = "lb_storage_area", text_size = 16},
        }},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
            application.events:send("encyclopedia.open_consumable", consumable)
        end},
    }
})