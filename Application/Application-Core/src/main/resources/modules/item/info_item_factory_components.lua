ui:extend({
    type = "view",
    name = "base.ui.panel_item_info.factory_components",
    parent = "base.ui.panel_item_info.details_content",
    controller = "org.smallbox.faraway.client.controller.ItemInfoFactoryComponentsController",
    position = {12, 12},
    visible = false,
    views = {
        { type = "label", text = "onCloseComponents", size = {50, 30}, action = "onCloseComponents" },
        { type = "list", id = "list_components" },
    },
})
