item = nil

data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 65},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views =
    {
        { type = "label", text = "Item", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "list", position = {0, 60}, views = {
            { type = "label", id = "lb_position", text_size = 18, padding = 10},
            { type = "label", id = "lb_complete", text_size = 18, padding = 10},
            { type = "list", id = "frame_building", position = {0, 40}, views = {
                { type = "label", id = "lb_building", text = "Building in progress", text_size = 22, padding = 10, size = {400, 26}},
                { type = "image", id = "img_building_progress", position = {10, 12}, src = "data/graphics/needbar.png", size = {380, 16}, texture_rect = {0, 0, 100, 16}},
                { type = "label", id = "lb_building_progress", text_size = 14, padding = 10, position = {0, 10}},
                { type = "label", id = "lb_building_job", text_size = 14, padding = 10, position = {0, 10}},
                { type = "label", id = "lb_building_character", text_size = 14, padding = 10, position = {0, 10}},
                { type = "label", text = "Components", text_size = 20, padding = 10, position = {0, 15}},
                { type = "list", id = "list_building_components", position = {0, 10}, adapter = {
                    view = { type = "label", text_size = 14, padding = 10 },
                    on_bind = function(view, data)
                        view:setDashedString(data.info.label .. " (" .. data.currentQuantity .. "/" .. data.neededQuantity .. ")", (data.job and (data.job:getCharacter() and data.job:getCharacter():getName() or (data.currentQuantity < data.neededQuantity and "waiting" or "complete")) or "no job"), 48)
                    end
                }},
            }},
            { type = "list", id = "frame_factory", position = {10, 0}, views = {
                { type = "label", text = "Factory", text_size = 22},
                { type = "label", text = "Current", text_size = 18, position = {0, 15}},
                { type = "label", id = "lb_factory_receipt", text = "lb_factory_receipt", position = {0, 20}},
                { type = "label", id = "lb_factory_progress", text = "lb_factory_progress", position = {0, 20}},
                { type = "label", id = "lb_factory_character", text = "lb_factory_character", position = {0, 20}},
                { type = "label", id = "lb_factory_components", position = {0, 20}},
                { type = "label", id = "lb_factory_inputs", position = {0, 20}},
                { type = "label", id = "lb_factory_products", position = {0, 20}},
                { type = "label", text = "Orders", text_size = 18, position = {0, 30}},
                { type = "list", id = "list_receipt", position = {0, 35}},
            }},
        }},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
            game.events:send("encyclopedia.open_item", item)
        end},
    },

    on_event = function(event, view, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:clearSelection();
            item = nil
        end

        if event == game.events.on_deselect then
            view:setVisible(false)
            item = nil
        end

        if event == game.events.on_item_selected then
            item = data;
            view:setVisible(true)
            view:findById("lb_name"):setText(item:getLabel())
            view:findById("lb_position"):setText("Position: " .. item:getX() .. "x" .. item:getY())

            if item:getInfo().factory and item:getInfo().factory.receipts then
                display_factory_info(view, item:getFactory(), item:getInfo().factory)
            else
                view:findById("frame_factory"):setVisible(false)
            end
        end
    end,

    on_refresh = function(view)
        if item ~= nil then
            view:findById("frame_building"):setVisible(not item:isComplete())

            view:findById("lb_complete"):setText("Complete", ": ", item:isComplete())
            if not item:isComplete() then
                view:findById("lb_building"):setDashedString("Building", item:getBuildProgress() > 0 and math.floor(item:getBuildProgress() * 100) or "waiting", 32)
                view:findById("img_building_progress"):setTextureRect(0, 80, math.floor(item:getBuildProgress() * 380 / 10) * 10, 16)
                view:findById("lb_building_progress"):setText("Progress: " .. item:getCurrentBuild() .. "/" .. item:getTotalBuild())
                view:findById("lb_building_job"):setText("Build job: " .. (item:getBuildJob() and "yes" or "no"))
                view:findById("lb_building_character"):setText("Builder: " .. (item:getBuilder() and item:getBuilder():getName() or "no"))
                view:findById("list_building_components"):getAdapter():setData(item:getComponents());
            end

            if item:getFactory() then
                local factory = item:getFactory()

                view:findById("lb_factory_progress"):setText("Status", ": ",
                    factory:getMessage() and item:getFactory():getMessage() or "unknown")

                view:findById("lb_factory_receipt"):setText("Receipt", ": ",
                    factory:getActiveReceipt() and factory:getActiveReceipt().receiptInfo.label or "none")

                view:findById("lb_factory_character"):setText("Crafter", ": ",
                    factory:getJob() and factory:getJob():getCharacter() and factory:getJob():getCharacter():getName() or "none")

                if item:getFactory():getShoppingList() then
                    local str = "shopping list: "
                    local iterator = item:getFactory():getShoppingList():iterator()
                    while iterator:hasNext() do
                        local product = iterator:next()
                        str = str .. product.consumable:getInfo().name .. " x" .. product.quantity
                    end
                    view:findById("lb_factory_components"):setText(str)
                end

                if item:getFactory():getComponents() then
                    local str = "components: "
                    local iterator = item:getFactory():getComponents():iterator()
                    while iterator:hasNext() do
                        local component = iterator:next()
                        str = str .. component.itemInfo.name .. " " .. component.currentQuantity .. "/" .. component.totalQuantity
                    end
                    view:findById("lb_factory_inputs"):setText(str)
                end
            end
        end
    end
})

function display_factory_info(view, factory, factoryInfo)
    view:findById("frame_factory"):setVisible(true)

    local list = view:findById("list_receipt")
    list:removeAllViews()

    local iterator = factory:getOrders():iterator()
    while iterator:hasNext() do
        local order = iterator:next()
        local receipt = order.receiptGroupInfo
        local frame_receipt = game.ui:createView()
        frame_receipt:setSize(400, 22)

        local lb_receipt = game.ui:createLabel()
        lb_receipt:setText(receipt.label)
        frame_receipt:addView(lb_receipt)

        local lb_mode = game.ui:createLabel()
        lb_mode:setText("mode")
        lb_mode:setSize(50, 22)
        lb_mode:setPosition(300, 0)
        lb_mode:setOnClickListener(function(v)
            lb_mode:setText("gg")
        end)
        frame_receipt:addView(lb_mode)

        local lb_up = game.ui:createLabel()
        lb_up:setText("up")
        lb_up:setSize(50, 22)
        lb_up:setPosition(200, 0)
        lb_up:setOnClickListener(function(v)
            factory:moveReceipt(receipt, -1)
            display_factory_info(view, factory, factoryInfo)
        end)
        frame_receipt:addView(lb_up)

        local lb_down = game.ui:createLabel()
        lb_down:setText("down")
        lb_down:setSize(50, 22)
        lb_down:setPosition(250, 0)
        lb_down:setOnClickListener(function(v)
            factory:moveReceipt(receipt, 1)
            display_factory_info(view, factory, factoryInfo)
        end)
        frame_receipt:addView(lb_down)

        local lb_active = game.ui:createLabel()
        lb_active:setText(order.isActive and "[x]" or "[ ]")
        lb_active:setSize(50, 22)
        lb_active:setPosition(358, 0)
        lb_active:setOnClickListener(function(v)
            order.isActive = not order.isActive
            display_factory_info(view, factory, factoryInfo)
        end)
        frame_receipt:addView(lb_active)

        list:addView(frame_receipt)
    end
end