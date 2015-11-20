local function refresh_materials(grid_material, current_item_by_category, category, strategy)
    grid_material:removeAllViews()
    local iterator = data.items:iterator()
    while iterator:hasNext() do
        local item = iterator:next()
        if strategy(item) and category == (item.category and item.category or "default") and item.material then
            local bt_material = application.ui:createImage()
            bt_material:setSize(32, 32)
            bt_material:setImage(item.material.iconPath)
            bt_material:setOnClickListener(function()
                current_item_by_category[category] = item
                application:setBuild(current_item_by_category[category])
                refresh_materials(grid_material, current_item_by_category, category, strategy)
            end)
            if item == current_item_by_category[category] then
                bt_material:setBackgroundColor(0xff0000)
            end
            grid_material:addView(bt_material)
        end
    end
    grid_material:setPosition(350 - (grid_material:getViews():size() * 32), 0)
end

local function get_previous_item(strategy, category, current_item_by_category, grid_material)
    local current_item = current_item_by_category[category]
    local iterator = data.items:iterator()
    while iterator:hasNext() do
        local item = iterator:next()
        if strategy(item) and category == (item.category and item.category or "default") then
            if current_item == item then
                refresh_materials(grid_material, current_item_by_category, category, strategy)
                return
            end
            current_item_by_category[category] = item
        end
    end
end

local function get_next_item(strategy, category, current_item_by_category, grid_material)
    local current_item = current_item_by_category[category]
    local return_next = false
    local iterator = data.items:iterator()
    while iterator:hasNext() do
        local item = iterator:next()
        if strategy(item) and category == (item.category and item.category or "default") then
            if return_next or not current_item then
                current_item_by_category[category] = item
                refresh_materials(grid_material, current_item_by_category, category, strategy)
                return
            end
            if current_item == item then
                return_next = true
            end
        end
    end
end

local function open_category(grid_items, strategy, category)
    print ("open category " .. category)

    -- Get items for this category
    local items = {}
    local iterator = data.items:iterator()
    while iterator:hasNext() do
        local item = iterator:next()
        if strategy(item) and category == (item.category and item.category or "default") then
            print ("found item " .. item.name)
            table.insert(items, item)
        end
    end

    -- Create items grid
    grid_items:removeAllViews()
    grid_items:setVisible(true)
    for key, value in pairs(items) do
        local bt_item = application.ui:createView()
        bt_item:setSize(350, 24)
        bt_item:setMargin(5, 0, 0, 0)
        bt_item:setBackgroundColor(0x448866)
        bt_item:setOnClickListener(function()
            application:setBuild(value)
        end)

        if value.fileName ~= nil then
            local ic_item = application.ui:createImage()
            ic_item:setImage("data/items/" .. value.fileName .. ".png")
            ic_item:setSize(32, 32)
            ic_item:setPosition(10, 10)
            bt_item:addView(ic_item)
        end

        local lb_item = application.ui:createLabel()
        lb_item:setText(value.label)
        lb_item:setTextSize(14)
        lb_item:setMargin(12, 0, 0, 6)
        bt_item:addView(lb_item)

        grid_items:addView(bt_item)
    end
end

local function open_main_category(grid_categories, grid_items, group_by_category, strategy)
    -- Get categories for this main category
    local categories = {}
    local iterator = data.items:iterator()
    while iterator:hasNext() do
        local item = iterator:next()
        local category = item.category and item.category or "default"
        if strategy(item) and not table.contains(categories, category) then
            table.insert(categories, category)
        end
    end

    -- Create grid
    if group_by_category then
        local current_item_by_category = {}
        local iterator = data.items:iterator()
        while iterator:hasNext() do
            local item = iterator:next()
            if strategy(item) and item.material then
                local category = (item.category and item.category or "default")
                if not current_item_by_category[category] then
                    current_item_by_category[category] = item
                end
            end
        end

        grid_categories:setVisible(false)
        grid_items:removeAllViews()
        for key, category in pairs(categories) do
            local view_item = application.ui:createView()
            view_item:setSize(350, 32)
            view_item:setBackgroundColor(0x349394)
            view_item:setOnFocusListener(function(v, is_active)
                view_item:setBackgroundColor(is_active and 0x25c9cb or 0x349394)
            end)

            local grid_material = application.ui:createGrid()
            grid_material:setColumns(10)
            grid_material:setColumnWidth(32)
            grid_material:setRowHeight(32)
            grid_material:setPosition(200, 0)
            view_item:addView(grid_material)
            refresh_materials(grid_material, current_item_by_category, category, strategy)

            local bt_category = application.ui:createLabel()
            bt_category:setSize(350, 32)
            bt_category:setText(category)
            bt_category:setPadding(12, 10)
            bt_category:setTextSize(16)
            bt_category:setOnMouseWheelUpListener(function(v)
                get_next_item(strategy, category, current_item_by_category, grid_material)
                application:setBuild(current_item_by_category[category])
            end)
            bt_category:setOnMouseWheelDownListener(function(v)
                get_previous_item(strategy, category, current_item_by_category, grid_material)
                application:setBuild(current_item_by_category[category])
            end)
            view_item:addView(bt_category)

            grid_items:addView(view_item)
        end
    else
        grid_categories:removeAllViews()
        grid_categories:setVisible(true)
        for key, category in pairs(categories) do
            local bt_category = application.ui:createLabel()
            bt_category:setSize(95, 36)
            bt_category:setText(category)
            bt_category:setPadding(12, 10)
            bt_category:setTextSize(16)
            bt_category:setBackgroundColor(0x349394)
            bt_category:setOnFocusListener(function(v, is_active)
                bt_category:setBackgroundColor(is_active and 0x25c9cb or 0x349394)
            end)
            bt_category:setOnClickListener(function(v)
                open_category(grid_items, strategy, category)
            end)
            grid_categories:addView(bt_category)
        end
    end
end

function table.contains(table, element)
    for _, value in pairs(table) do
        if value == element then
            return true
        end
    end
    return false
end

data:extend({
    type = "view",
    style = "base.style.right_panel",
    id = "base.ui.panel_build",
    on_click = function() end,
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            application.ui:findById("base.ui.panel_main"):setVisible(true)
            application.ui:findById("base.ui.panel_build"):setVisible(false)
        end},
        { type = "label", text = "Build", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", position = {10, 50}, views = {
            { type = "grid", id="grid_main_categories", columns = 4, column_width = 100, row_height = 40, margin = {0, 0, 20, 0}},
            { type = "grid", id="grid_categories", columns = 4, column_width = 100, row_height = 40, margin = {0, 0, 20, 0}},
            { type = "list", id="list_items"},
        }},
    },

    on_load = function(view)
        local strategy;

        local grid_main_categories = view:findById("grid_main_categories")
        local grid_categories = view:findById("grid_categories")
        local grid_items = view:findById("list_items")

        grid_main_categories:removeAllViews()
        local main_categories = {
            {"Structures", true, function(item) return item.isStructure end },
            {"Items", false, function(item) return item.isUserItem end },
            {"Networks", false, function(item) return item.isNetworkItem end },
        }
        for key, value in ipairs(main_categories) do
            local bt_main_category = application.ui:createLabel()
            bt_main_category:setSize(95, 36)
            bt_main_category:setPadding(12, 10)
            bt_main_category:setText(value[1])
            bt_main_category:setTextSize(16)
            bt_main_category:setBackgroundColor(0x778855)
            bt_main_category:setOnFocusListener(function(v, is_active)
                bt_main_category:setBackgroundColor(is_active and 0x25c9cb or 0x349394)
            end)
            bt_main_category:setOnClickListener(function()
                open_main_category(grid_categories, grid_items, value[2], value[3])
            end)
            grid_main_categories:addView(bt_main_category)
        end

        local categories = {}
        for i = 0, data.items:size() - 1 do
            local item = data.items:get(i)
            if item.isUserItem or item.isStructure then
                local index = item.category and item.category or "default"
                if categories[index] == nil then
                    categories[index] = {}
                end
                table.insert(categories[index], item)
            end
        end
    end,
})
