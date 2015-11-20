local current_area
local need_refresh = true
g_category = nil

local function setPriority(view, priority)
    local iterator = view:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x00000000)
    end
    view:setBackgroundColor(0xff0000)
    current_area:setPriority(priority)
end

local function setCategory(view, category)
    g_category = category
    need_refresh = true
    local iterator = view:getParent():getViews():iterator()
    while iterator:hasNext() do
        iterator:next():setBackgroundColor(0x556644)
    end
    view:setBackgroundColor(0x885566)
end

local function open_category(category, items)
    local list_item = {}
    local iterator = current_area:getItemsAccepts():entrySet():iterator()
    while iterator:hasNext() do
        local entry = iterator:next()
        if entry:getKey().category == category then
            table.insert(list_item, entry)
        end
    end

    local window = application.ui:findById("base.ui.info_area_storage")
    local grid = window:findById("grid_items")
    grid:removeAllViews()
    grid:keepSorted(true)
    for key in pairs(list_item) do
        local entry = list_item[key]
        local lb_entry = application.ui:createLabel()
        lb_entry:setText((entry:getValue() and "[x] " or "[ ] "), entry:getKey().label)
        lb_entry:setTextSize(14)
        lb_entry:setSize(180, 20)
        lb_entry:setPadding(5)
        lb_entry:setOnClickListener(function()
            current_area:setAccept(entry:getKey(), not entry:getValue())
            open_category(category)
        end)
        grid:addView(lb_entry)
    end

    window:findById("bt_accept_all_category"):setOnClickListener(function()
        for key in pairs(list_item) do
            current_area:setAccept(list_item[key]:getKey(), true)
            open_category(category)
        end
    end)

    window:findById("bt_refuse_all_category"):setOnClickListener(function()
        for key in pairs(list_item) do
            current_area:setAccept(list_item[key]:getKey(), false)
            open_category(category)
        end
    end)
end

data:extend({
    type = "view",
    id = "base.ui.info_area_storage",
    style = "base.style.right_panel",
    on_click = function() end,
    visible = false,
    views = {
        { type = "list", views = {
            { type = "view", size = {400, 40}, views = {
                { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 45}, position = {0, 8}},
                { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {90, 32}, on_click = function()
                    application.events:send("encyclopedia.open_area", current_area)
                end},
            }},
            { type = "grid", position = {10, 18}, columns = 2, column_width = 195, row_height = 60, views = {
                { type = "label", id = "bt_inventory", text = "Content", text_size = 20, padding = 18, background = 0x4be7da, size = {180, 50},
                    on_click = function()
                        application.ui:findById("base.ui.info_area_storage"):findById("bt_inventory"):setBackgroundColor(0x4be7da);
                        application.ui:findById("base.ui.info_area_storage"):findById("bt_rules"):setBackgroundColor(0x689999);
                        application.ui:findById("base.ui.info_area_storage"):findById("frame_inventory"):setVisible(true);
                        application.ui:findById("base.ui.info_area_storage"):findById("frame_rules"):setVisible(false);
                    end},
                { type = "label", id = "bt_rules", text = "Rules", text_size = 20, padding = 18, background = 0x689999, size = {180, 50},
                    on_click = function()
                        application.ui:findById("base.ui.info_area_storage"):findById("bt_inventory"):setBackgroundColor(0x689999);
                        application.ui:findById("base.ui.info_area_storage"):findById("bt_rules"):setBackgroundColor(0x4be7da);
                        application.ui:findById("base.ui.info_area_storage"):findById("frame_inventory"):setVisible(false);
                        application.ui:findById("base.ui.info_area_storage"):findById("frame_rules"):setVisible(true);
                    end},
            }},
            { type = "list", id = "frame_inventory", position = {0, 18}, views = {
                { type = "list", id = "list_storage_content", position = {5, 5}},
            }},
            { type = "list", id = "frame_rules", visible = false, position = {0, 18}, views = {
                { type = "view", size = {400, 32}, views = {
                    { type = "label", text = "Priority", text_size = 18, padding = 10},
                    { type = "label", text = "[1]", id = "bt_priority_1", text_size = 18, padding = 10, size = {32, 32}, position = {230, 0}, on_click = function(v) setPriority(v, 1) end},
                    { type = "label", text = "[2]", id = "bt_priority_2", text_size = 18, padding = 10, size = {32, 32}, position = {260, 0}, on_click = function(v) setPriority(v, 2) end},
                    { type = "label", text = "[3]", id = "bt_priority_3", text_size = 18, padding = 10, size = {32, 32}, position = {290, 0}, on_click = function(v) setPriority(v, 3) end},
                    { type = "label", text = "[4]", id = "bt_priority_4", text_size = 18, padding = 10, size = {32, 32}, position = {320, 0}, on_click = function(v) setPriority(v, 4) end},
                    { type = "label", text = "[5]", id = "bt_priority_5", text_size = 18, padding = 10, size = {32, 32}, position = {350, 0}, on_click = function(v) setPriority(v, 5) end},
                }},
                { type = "grid", id = "grid_category", columns = 4, column_width = 98, row_height = 40, position = {10, 10}},
                { type = "view", size = {350, 32}, position = {10, 40}, views = {
                    { type = "label", text = "accept all", id = "bt_accept_all_category", text_size = 16, padding = 5, size = {100, 32}, position = {0, 0}},
                    { type = "label", text = "refuse all", id = "bt_refuse_all_category", text_size = 16, padding = 5, size = {100, 32}, position = {200, 0}},
                }},
                { type = "grid", id = "grid_items", columns = 2, column_width = 190, row_height = 24, position = {5, 30}},
            }},
        }}
    },

    on_load = function(view)
        local items_by_category = {}
        local iterator = data.items:iterator()
        while iterator:hasNext() do
            local item = iterator:next()
            if item.isConsumable then
                local category = (item.category and item.category or "none")
                if not items_by_category[category] then
                    items_by_category[category] = {}
                end
                table.insert(items_by_category[category], item)
            end
        end

        local grid = view:findById("grid_category")
        grid:removeAllViews()
        for key, value in pairs(items_by_category) do
            if key ~= "special" then
                local lb_category = application.ui:createLabel()
                lb_category:setText(key)
                lb_category:setTextSize(16)
                lb_category:setSize(90, 32)
                lb_category:setPadding(10, 8)
                lb_category:setBackgroundColor(0x556644)
                lb_category:setOnClickListener(function()
                    open_category(key, value)
                end)
                grid:addView(lb_category)
            end
        end
    end,

    on_event = function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            current_area = nil
            view:setVisible(false)
            application.game:clearSelection();
        end

        if event == application.events.on_deselect then
            current_area = nil
            view:setVisible(false)
        end

        if event == application.events.on_area_selected and data:getTypeName() == "STORAGE" then
            current_area = data;
            need_refresh = true
            view:setVisible(true)
            view:findById("lb_name"):setText(data:getName())
        end
    end,

    on_refresh = function(view)
        if current_area and need_refresh then
            need_refresh = false

        end

        if current_area and current_area:isStorage() then
            local priority = current_area:getPriority()
            view:findById("bt_priority_1"):setBackgroundColor(priority == 1 and 0xff0000 or 0x000000)
            view:findById("bt_priority_2"):setBackgroundColor(priority == 2 and 0xff0000 or 0x000000)
            view:findById("bt_priority_3"):setBackgroundColor(priority == 3 and 0xff0000 or 0x000000)
            view:findById("bt_priority_4"):setBackgroundColor(priority == 4 and 0xff0000 or 0x000000)
            view:findById("bt_priority_5"):setBackgroundColor(priority == 5 and 0xff0000 or 0x000000)

            local item_map = {}
            local parcel_iterator = current_area:getParcels():iterator()
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
                local lb_item = application.ui:createLabel()
                lb_item:setDashedString(key, value, 47)
                lb_item:setTextSize(14)
                lb_item:setSize(180, 20)
                lb_item:setPadding(5)
                list_storage_content:addView(lb_item)
            end

        end

    end
})