ui:extend({
    type = "view",
    name = "base.ui.info_factory_receipt",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.ItemInfoReceiptController",
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "list", id = "list_actions"},
    },
})
