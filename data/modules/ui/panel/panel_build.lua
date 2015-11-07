data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_build",
    visible = false,
    views = {
        { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
            application.ui:findById("panel_main"):setVisible(true)
            application.ui:findById("panel_build"):setVisible(false)
        end},
        { type = "label", text = "Build", text_size = 28, padding = 10, position = {40, 0}},
        { type = "list", position = {10, 50}, views = {
            { type = "grid", id="grid_main_categories", columns = 4, column_width = 100, row_height = 40, margin = {0, 0, 20, 0}},
            { type = "grid", id="grid_categories", columns = 4, column_width = 100, row_height = 40, margin = {0, 0, 20, 0}},
            { type = "grid", id="list_items", columns = 5, column_width = 78, row_height = 95 },
        }},
    },

    on_load = function(view)
        local strategy;

        local grid_main_categories = view:findById("grid_main_categories")
        local grid_categories = view:findById("grid_categories")
        local grid_items = view:findById("list_items")

        grid_main_categories:removeAllViews()
        local main_categories = {
            {"Structures", function(item) return item.isStructure end },
            {"Items", function(item) return item.isUserItem end },
            {"Networks", function(item) return item.isNetworkItem end },
        }
        for key, value in ipairs(main_categories) do
            local bt_main_category = application.ui:createLabel()
            bt_main_category:setSize(95, 36)
            bt_main_category:setText(value[1])
            bt_main_category:setTextSize(18)
            bt_main_category:setBackgroundColor(0x778855)
            bt_main_category:setOnFocusListener(function(v, is_active)
                bt_main_category:setBackgroundColor(is_active and 0x25c9cb or 0x349394)
            end)
            bt_main_category:setOnClickListener(function()
                open_main_category(grid_categories, grid_items, value[2])
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

function open_main_category(grid_categories, grid_items, strategy)
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
    grid_categories:removeAllViews()
    grid_categories:setVisible(true)
    for key, value in pairs(categories) do
        local bt_category = application.ui:createLabel()
        bt_category:setSize(95, 36)
        bt_category:setText(value)
        bt_category:setTextSize(18)
        bt_category:setBackgroundColor(0x349394)
        bt_category:setOnFocusListener(function(v, is_active)
            bt_category:setBackgroundColor(is_active and 0x25c9cb or 0x349394)
        end)
        bt_category:setOnClickListener(function(v)
            open_category(grid_items, strategy, value)
        end)
        grid_categories:addView(bt_category)
    end

end

function open_category(grid_items, strategy, category)
    -- Get items for this category
    local items = {}
    local iterator = data.items:iterator()
    while iterator:hasNext() do
        local item = iterator:next()
        if strategy(item) and category == (item.category and item.category or "default") then
            table.insert(items, item)
        end
    end

    -- Create items grid
    grid_items:removeAllViews()
    grid_items:setVisible(true)
    for key, value in pairs(items) do
        local bt_item = application.ui:createView()
        bt_item:setSize(68, 90)
        bt_item:setBackgroundColor(0x448866)
        bt_item:setOnClickListener(function()
            application:setBuild(value)
        end)

        if value.fileName ~= nil then
            local image = application.ui:createImage()
            image:setImage("data/items/" .. value.fileName .. ".png")
            image:setSize(32, 32)
            image:setPosition(10, 10)
            bt_item:addView(image)
        end

        local label = application.ui:createLabel()
        label:setMaxLength(10)
        label:setText(value.label)
        label:setTextSize(14)
        label:setSize(400, 20)
        label:setPosition(0, 50)
        bt_item:addView(label)

        grid_items:addView(bt_item)
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