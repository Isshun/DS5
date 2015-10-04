game.data:extend(
{
    {
        type = "view",
        position = {1200, 65},
        size = {400, 800},
        background = 0x121c1e,
        id = "panel_build",
        visible = false,
        views = {
            { type = "label", text = "< ", text_size = 34, position = {16, 7}, size = {32, 32}, on_click = function(view)
                game.ui:findById("panel_main"):setVisible(true)
                game.ui:findById("panel_build"):setVisible(false)
            end},
            { type = "label", text = "Build", text_size = 28, padding = 10, position = {40, 0}},
            { type = "grid", id="grid_categories", position = {10, 40}, columns = 4, column_width = 100, row_height = 40 },
            { type = "grid", id="list_items", position = {10, 0}, columns = 5, column_width = 78, row_height = 95 },
        },
        on_load = function(view)
            list = view:findById("grid_categories")
            list:removeAllViews()
        
            local categories = {}
            for i = 0, game.data.items:size() - 1 do
                local item = game.data.items:get(i)
                if item.isUserItem or item.isStructure then
                    local index = item.category and item.category or "default"
                    if categories[index] == nil then
                        categories[index] = {}
                    end
                    table.insert(categories[index], item)
                end
            end

            local nb_categories = 0
            for key, value in pairs(categories) do
                bt_category = game.ui:createLabel()
                bt_category:setSize(95, 36)
                bt_category:setText(key)
                bt_category:setTextSize(18)
                bt_category:setBackgroundColor(0x885577)
                bt_category:setOnClickListener(function(view)
                    openCategory(key, value)
                end)
                list:addView(bt_category)
                nb_categories = nb_categories + 1
            end
            
            view:findById("list_items"):setPosition(10, 40 + math.floor(42 * (nb_categories / 4)))
            print(nb_categories)
            
            function openCategory(category, items)
                list = view:findById("list_items")
                list:removeAllViews()
                list:setVisible(true)

                for key, value in pairs(items) do
                    local bt_item = game.ui:createView()
                    bt_item:setSize(68, 90)
                    bt_item:setBackgroundColor(0x448866)
                    bt_item:setOnClickListener(function(view)
                        game:setBuild(value)
                        game.ui:setCursor(game.ui:createCursor("build_cursor"))
                    end)

                    image = game.ui:createImage()
                    image:setImagePath("data/items/" .. value.fileName .. ".png")
                    image:setSize(32, 32)
                    image:setPosition(10, 10)
                    bt_item:addView(image)
                
                    label = game.ui:createLabel()
                    label:setText(value.label)
                    label:setTextSize(14)
                    label:setSize(400, 20)
                    label:setPosition(0, 50)
                    bt_item:addView(label)

                    list:addView(bt_item)
                end
            end
            
        end,
        on_event = function(eventId, view, data)
            if (eventId == game.events.on_job_create) then
                game:clearAction()
                game.ui:clearCursor()
            end
        end
    },
}
)