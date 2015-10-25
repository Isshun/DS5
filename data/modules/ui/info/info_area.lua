g_area = nil
g_category = nil
g_need_refresh = nil

function setPriority(view, priority)
    local iterator = view:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x00000000)
    end
    view:setBackgroundColor(0xff0000)
    g_area:setPriority(priority)
end

function setCategory(view, category)
    g_category = category
    g_need_refresh = true
    local iterator = view:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x556644)
    end
    view:setBackgroundColor(0x885566)
end

data:extend({
    type = "list",
    id = "info_area",
    position = {1200, 65},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
            game.events:send("encyclopedia.open_area", g_area)
        end},
        { type = "grid", position = {10, 10}, columns = 2, column_width = 190, row_height = 60, views = {
            { type = "label", id = "bt_inventory", text = "Content", text_size = 20, padding = 18, background = 0x4be7da, size = {180, 50},
                on_click = function()
                    game.ui:findById("info_area"):findById("bt_inventory"):setBackgroundColor(0x4be7da);
                    game.ui:findById("info_area"):findById("bt_rules"):setBackgroundColor(0x689999);
                    game.ui:findById("info_area"):findById("frame_inventory"):setVisible(true);
                    game.ui:findById("info_area"):findById("frame_rules"):setVisible(false);
                end},
            { type = "label", id = "bt_rules", text = "Rules", text_size = 20, padding = 18, background = 0x689999, size = {180, 50},
                on_click = function()
                    game.ui:findById("info_area"):findById("bt_inventory"):setBackgroundColor(0x689999);
                    game.ui:findById("info_area"):findById("bt_rules"):setBackgroundColor(0x4be7da);
                    game.ui:findById("info_area"):findById("frame_inventory"):setVisible(false);
                    game.ui:findById("info_area"):findById("frame_rules"):setVisible(true);
                end},
        }},
        { type = "list", position = {0, 40}, views = {
            { type = "label", id = "lb_position", text_size = 18, padding = 10},
            { type = "label", id = "lb_quantity", text_size = 18, padding = 10},
        }},
        { type = "list", id = "frame_inventory", position = {0, 40}, views = {
            { type = "label", text = "Inventory", text_size = 18, padding = 10},
            { type = "list", id = "list_storage_content", position = {5, 75}},
        }},
        { type = "list", id = "frame_rules", visible = false, position = {0, 40}, views = {
            { type = "view", size = {400, 32}, views = {
                { type = "label", text = "Priority", text_size = 18, padding = 10},
                { type = "label", text = "[1]", id = "bt_priority_1", text_size = 18, padding = 10, size = {32, 32}, position = {230, 0}, on_click = function(v) setPriority(v, 1) end},
                { type = "label", text = "[2]", id = "bt_priority_2", text_size = 18, padding = 10, size = {32, 32}, position = {260, 0}, on_click = function(v) setPriority(v, 2) end},
                { type = "label", text = "[3]", id = "bt_priority_3", text_size = 18, padding = 10, size = {32, 32}, position = {290, 0}, on_click = function(v) setPriority(v, 3) end},
                { type = "label", text = "[4]", id = "bt_priority_4", text_size = 18, padding = 10, size = {32, 32}, position = {320, 0}, on_click = function(v) setPriority(v, 4) end},
                { type = "label", text = "[5]", id = "bt_priority_5", text_size = 18, padding = 10, size = {32, 32}, position = {350, 0}, on_click = function(v) setPriority(v, 5) end},
            }},
            { type = "grid", columns = 4, column_width = 98, row_height = 40, position = {10, 10}, views = {
                { type = "label", text = "none", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view) setCategory(view, nil) end},
                { type = "label", text = "mineral", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view) setCategory(view, "mineral") end},
                { type = "label", text = "organic", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view) setCategory(view, "organic") end},
                { type = "label", text = "component", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view) setCategory(view, "component") end},
                { type = "label", text = "food", text_size = 16, padding = 10, size = {88, 32}, background = 0x556644, on_click = function(view) setCategory(view, "food") end},
            }},
            { type = "grid", id = "grid_items", columns = 2, column_width = 190, row_height = 24, position = {5, 30}},
        }},
    },

    on_event =
    function(event, view, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            g_area = nil
            view:setVisible(false)
            game.ui:clearSelection();
        end

        if event == game.events.on_deselect then
            g_area = nil
            view:setVisible(false)
        end

        if event == game.events.on_area_selected then
            g_area = data;
            g_need_refresh = true
            view:setVisible(true)
            view:findById("lb_name"):setText(data:getName())
        end
    end,

    on_refresh =
    function(view)
        if g_area and g_need_refresh then
            g_need_refresh = false

            local grid = view:findById("grid_items")
            grid:removeAllViews()
            grid:keepSorted(true)

            local list_item = {}
            local iterator = g_area:getItemsAccepts():entrySet():iterator()
            while iterator:hasNext() do
                local entry = iterator:next()
                if entry:getKey().category == g_category then
                    table.insert(list_item, entry)
                end
            end

            for key in pairs(list_item) do
                local entry = list_item[key]
                local lb_entry = game.ui:createLabel()
                lb_entry:setText((entry:getValue() and "[x] " or "[ ] "), entry:getKey().label)
                lb_entry:setTextSize(14)
                lb_entry:setSize(180, 20)
                lb_entry:setPadding(5)
                lb_entry:setOnClickListener(function(subview)
                    g_area:setAccept(entry:getKey(), not entry:getValue())
                    g_need_refresh = true
                end)
                grid:addView(lb_entry)
            end
        end

        if g_area and g_area:isStorage() then
            local priority = g_area:getPriority()
            view:findById("bt_priority_1"):setBackgroundColor(priority == 1 and 0xff0000 or 0x000000)
            view:findById("bt_priority_2"):setBackgroundColor(priority == 2 and 0xff0000 or 0x000000)
            view:findById("bt_priority_3"):setBackgroundColor(priority == 3 and 0xff0000 or 0x000000)
            view:findById("bt_priority_4"):setBackgroundColor(priority == 4 and 0xff0000 or 0x000000)
            view:findById("bt_priority_5"):setBackgroundColor(priority == 5 and 0xff0000 or 0x000000)

            local item_map = {}
            local parcel_iterator = g_area:getParcels():iterator()
            while parcel_iterator:hasNext() do
                local parcel = parcel_iterator:next()
                if parcel:getConsumable() then
                    local consumable = parcel:getConsumable()
                    local label = consumable:getInfo().label
                    if item_map[label] then
                        item_map[label] = item_map[label] + consumable:getQuantity()
                    else
                        item_map[label] = consumable:getQuantity()
                    end
                end
            end

            local list_storage_content = view:findById("list_storage_content")
            list_storage_content:removeAllViews()
            for key, value in pairs(item_map) do
                local lb_item = game.ui:createLabel()
                lb_item:setDashedString(key, value, 48)
                lb_item:setTextSize(14)
                lb_item:setSize(180, 20)
                lb_item:setPadding(5)
                list_storage_content:addView(lb_item)
            end

        end

    end
})