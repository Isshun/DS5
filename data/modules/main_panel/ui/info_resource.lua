resource = nil

data:extend(
    {
        {
            type = "view",
            name = "ui-test",
            position = {1200, 65},
            size = {400, 800},
            background = 0x121c1e,
            visible = false,
            views =
            {
                { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
                { type = "label", text = "Resource", text_size = 12, position = {10, 8}},
                { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
                { type = "list", position = {0, 60}, views = {
                    { type = "label", id = "lb_position", text_size = 18, padding = 10},
                    { type = "label", id = "lb_quantity", text_size = 18, padding = 10},
                    { type = "label", id = "lb_maturity", text_size = 18, padding = 10},
                    { type = "label", id = "lb_grow_state", text_size = 18, padding = 10},
                }},
                { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 30}, size = {90, 40}, on_click = function()
                    game.events:send("encyclopedia.open_resource", resource)
                end},
            },

            on_event =
            function(event, view, data)
                if event == game.events.on_key_press and data == "ESCAPE" then
                    view:setVisible(false)
                    game.ui:clearSelection();
                    resource = nil
                end

                if event == game.events.on_deselect then
                    view:setVisible(false)
                    resource = nil
                end

                if event == game.events.on_resource_selected then
                    view:setVisible(true)
                    view:findById("lb_name"):setText(data:getLabel())
                    resource = data;
                end
            end,

            on_refresh =
            function(view)
                if resource ~= nil then
                    local info = resource:getInfo()
                    view:findById("lb_position"):setText("Position", ": ", resource:getX() .. "x" .. resource:getY())
                    view:findById("lb_quantity"):setText("Quantity", ": ", resource:getQuantity())
                    if info.isPlant then
                        view:findById("lb_maturity"):setText("Maturity", ": ", math.floor(resource:getMaturity() * 100) .. "%")
                        view:findById("lb_maturity"):setVisible(true)
                        local growState = resource:getGrowState()
                        if growState then
                            view:findById("lb_grow_state"):setText("Grow state", ": ", growState.name, " (" .. (growState.value * 100) .. "%)")
                            view:findById("lb_grow_state"):setVisible(true)
                        end
                    else
                        view:findById("lb_maturity"):setVisible(false)
                        view:findById("lb_grow_state"):setVisible(false)
                    end
                end
            end
        },
    }
)