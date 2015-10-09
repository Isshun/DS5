consumable = nil

game.data:extend(
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
                { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
                { type = "list", position = {0, 40}, views = {
                    { type = "label", id = "lb_position", text_size = 18, padding = 10},
                    { type = "label", id = "lb_quantity", text_size = 18, padding = 10},
                }},
                { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
                    game.events:send("encyclopedia.open_consumable", consumable)
                end},
            },

            on_event =
            function(event, view, data)
                if event == game.events.on_key_press and data == "ESCAPE" then
                    view:setVisible(false)
                    game.ui:clearSelection();
                    consumable = nil
                end

                if event == game.events.on_deselect then
                    view:setVisible(false)
                    consumable = nil
                end

                if event == game.events.on_consumable_selected then
                    view:setVisible(true)
                    view:findById("lb_name"):setText(data:getLabel())
                    consumable = data;
                end
            end,

            on_refresh =
            function(view)
                if consumable ~= nil then
                    local info = consumable:getInfo()
                    view:findById("lb_position"):setText("Position: " .. consumable:getX() .. "x" .. consumable:getY())
                    view:findById("lb_quantity"):setText("Quantity: " .. consumable:getQuantity())
                end
            end
        },
    }
)