ui:extend({
    type = "view",
    id = "base.ui.right_panel.areas",
    parent = "base.ui.right_panel",
    controller = "org.smallbox.faraway.client.controller.AreaController",
    visible = false,
    views = {
        { type = "label", text = "Areas", text_color = 0x679B99, text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", id = "list_areas", position = {10, 40}, views = {
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Storage", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btAddStorage"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Dump", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btAddDump"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Home", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btAddHome"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Sector", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btAddSector"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Garden", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btAddGarden"},
        }},
        { type = "list", id = "list_areas", position = {205, 40}, views = {
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Storage", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btRemoveStorage"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Dump", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btRemoveDump"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Home", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btRemoveHome"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Sector", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btRemoveSector"},
            { type = "label", size = {160, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Garden", text_color = 0xB4D4D3, text_size = 18, padding = 10, id = "btRemoveGarden"},
        }},
    },
})