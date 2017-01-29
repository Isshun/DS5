ui:extend({
    type = "view",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    id = "base.ui.panel_areas",
    controller = "org.smallbox.faraway.module.area.AreaController",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("base.ui.panel_areas"):setVisible(false)
        end},
        { type = "label", text = "Areas", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", id = "list_areas", position = {10, 40}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Storage", text_size = 18, padding = 10, id = "btAddStorage"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Dump", text_size = 18, padding = 10, id = "btAddDump"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Home", text_size = 18, padding = 10, id = "btAddHome"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Sector", text_size = 18, padding = 10, id = "btAddSector"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Garden", text_size = 18, padding = 10, id = "btAddGarden"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Storage", text_size = 18, padding = 10, on_click = "application:setArea('storage')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Dump", text_size = 18, padding = 10, on_click = "application:setArea('dump')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Home", text_size = 18, padding = 10, on_click = "application:setArea('home')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Sector", text_size = 18, padding = 10, on_click = "application:setArea('sector')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "+ Garden", text_size = 18, padding = 10, on_click = "application:setArea('garden')"},
        }},
        { type = "list", id = "list_areas", position = {205, 40}, views = {
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Storage", text_size = 18, padding = 10, id = "btRemoveStorage"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Dump", text_size = 18, padding = 10, id = "btRemoveDump"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Home", text_size = 18, padding = 10, id = "btRemoveHome"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Sector", text_size = 18, padding = 10, id = "btRemoveSector"},
            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Garden", text_size = 18, padding = 10, id = "btRemoveGarden"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Storage", text_size = 18, padding = 10, on_click = "application:removeArea('storage')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Dump", text_size = 18, padding = 10, on_click = "application:removeArea('dump')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Home", text_size = 18, padding = 10, on_click = "application:removeArea('home')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Sector", text_size = 18, padding = 10, on_click = "application:removeArea('sector')"},
--            { type = "label", size = {180, 40}, background = {regular = 0x121c1e, focus = 0x25c9cb}, text = "- Garden", text_size = 18, padding = 10, on_click = "application:removeArea('garden')"},
        }},
    },
--    on_event = function(view, event , data)
--        if event == application.events.on_key_press and data == "ESCAPE" then
--            view:setVisible(false)
--            ui:find("base.ui.panel_main"):setVisible(true)
--            application:sendEvent("mini_map.display", true)
--        end
--    end
})