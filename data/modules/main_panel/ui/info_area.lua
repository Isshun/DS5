area = nil
category = nil
need_refresh = nil

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
                { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
                { type = "list", position = {0, 40}, views = {
                    { type = "label", id = "lb_position", text_size = 18, padding = 10},
                    { type = "label", id = "lb_quantity", text_size = 18, padding = 10},
                    { type = "grid", columns = 6, column_width = 98, row_height = 22, position = {10, 0}, views = {
                        { type = "label", text = "none", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view)
                            category = nil
                            need_refresh = true
                            local iterator = view:getParent():getViews():iterator()
                            while iterator:hasNext() do
                                iterator:next():setBackgroundColor(0x556644)
                            end
                            view:setBackgroundColor(0x885566)
                        end},
                        { type = "label", text = "mineral", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view)
                            category = "mineral"
                            need_refresh = true
                            local iterator = view:getParent():getViews():iterator()
                            while iterator:hasNext() do
                                iterator:next():setBackgroundColor(0x556644)
                            end
                            view:setBackgroundColor(0x885566)
                        end},
                        { type = "label", text = "organic", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view)
                            category = "organic"
                            need_refresh = true
                            local iterator = view:getParent():getViews():iterator()
                            while iterator:hasNext() do
                                iterator:next():setBackgroundColor(0x556644)
                            end
                            view:setBackgroundColor(0x885566)
                        end},
                        { type = "label", text = "component", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view)
                            category = "component"
                            need_refresh = true
                            local iterator = view:getParent():getViews():iterator()
                            while iterator:hasNext() do
                                iterator:next():setBackgroundColor(0x556644)
                            end
                            view:setBackgroundColor(0x885566)
                        end},
                    }},
                    { type = "grid", id = "grid_items", columns = 2, column_width = 190, row_height = 24, position = {5, 30}},
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
                    need_refresh = true
                    view:setVisible(true)
                    view:findById("lb_name"):setText(data:getName())
                end
            end,

            on_refresh =
            function(view)
                if area ~= nil and need_refresh then
                    need_refresh = false

                    local grid = view:findById("grid_items")
                    grid:removeAllViews()

                    local list_item = {}
                    local iterator = area:getItemsAccepts():entrySet():iterator()
                    while iterator:hasNext() do
                        local entry = iterator:next()
                        if entry:getKey().category == category then
                            table.insert(list_item, entry)
                        end
                    end

                    for key in pairs(list_item) do
                        local entry = list_item[key]
                        local lb_entry = game.ui:createLabel()
                        lb_entry:setText((entry:getValue() and "[x] " or "[ ] ") .. entry:getKey().label)
                        lb_entry:setTextSize(14)
                        lb_entry:setSize(180, 20)
                        lb_entry:setPadding(5)
                        lb_entry:setOnClickListener(function(subview)
                            area:setAccept(entry:getKey(), not entry:getValue())
                            need_refresh = true
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