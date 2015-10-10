area = nil

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
                    { type = "grid", id = "grid_items", columns = 2, column_width = 180, row_height = 22 },
                }},
                { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
                    game.events:send("encyclopedia.open_area", area)
                end},
            },

            on_event =
            function(event, view, data)
                if event == game.events.on_key_press and data == "ESCAPE" then
                    area = nil
                    view:setVisible(false)
                    game.ui:clearSelection();
                end

                if event == game.events.on_deselect then
                    area = nil
                    view:setVisible(false)
                end

                if event == game.events.on_area_selected then
                    area = data;
                    view:setVisible(true)
                    view:findById("lb_name"):setText(data:getName())
                end
            end,

            on_refresh =
            function(view)
                if area ~= nil then
                    local grid = view:findById("grid_items")
                    grid:removeAllViews()

                    local iterator = area:getItemsAccepts():entrySet():iterator()
                    while iterator:hasNext() do
                        local entry = iterator:next()
                        local lb_entry = game.ui:createLabel()
                        lb_entry:setText((entry:getValue() and "[x] " or "[ ] ") .. entry:getKey().label)
                        lb_entry:setTextSize(14)
                        lb_entry:setSize(180, 22)
                        lb_entry:setPadding(10)
                        lb_entry:setOnClickListener(function(subview)
                            area:setAccept(entry:getKey(), not entry:getValue())
                        end)
                        grid:addView(lb_entry)
                    end

--                    local info = area:getInfo()
--                    view:findById("lb_position"):setText("Position: " .. area:getX() .. "x" .. area:getY())
--                    view:findById("lb_quantity"):setText("Quantity: " .. area:getQuantity())
                end
            end
        },
    }
)