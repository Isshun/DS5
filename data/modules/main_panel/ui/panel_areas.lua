game.data:extend(
{
    {
        type = "view",
        position = {1200, 65},
        size = {400, 800},
        background = 0x121c1e,
        id = "panel_areas",
        visible = false,
        views = {
            { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
                game.ui:findById("panel_main"):setVisible(true)
                game.ui:findById("panel_areas"):setVisible(false)
            end},
            { type = "label", text = "Areas", text_size = 28, padding = 10, position = {40, 0}},
            { type = "list", id = "list_areas", position = {10, 40}, views = {
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "+ Storage", text_size = 18, padding = 10, on_click = "game:setArea('storage')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "+ Dump", text_size = 18, padding = 10, on_click = "game:setArea('dump')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "+ Home", text_size = 18, padding = 10, on_click = "game:setArea('home')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "+ Sector", text_size = 18, padding = 10, on_click = "game:setArea('sector')"},
            }},
            { type = "list", id = "list_areas", position = {205, 40}, views = {
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "- Storage", text_size = 18, padding = 10, on_click = "game:removeArea('storage')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "- Dump", text_size = 18, padding = 10, on_click = "game:removeArea('dump')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "- Home", text_size = 18, padding = 10, on_click = "game:removeArea('home')"},
                { type = "label", size = {180, 40}, background = 0x8b9076, text = "- Sector", text_size = 18, padding = 10, on_click = "game:removeArea('sector')"},
            }},
        },
        on_event = function(event, view, data)
            if event == game.events.on_key_press and data == "ESCAPE" then
                view:setVisible(false)
                game.ui:findById("panel_main"):setVisible(true)
            end
        end
    },
}
)