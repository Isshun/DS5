local item

ui:extend({
    type = "view",
    name = "ui-test",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    controller = "org.smallbox.faraway.module.item.ItemInfoReceiptController",
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "list", id = "list_actions"},
    },
})
