local item

ui:extend({
    type = "view",
    name = "ui-test",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    controller = "org.smallbox.faraway.module.item.ItemInfoController",
    visible = false,
    views = {
        { type = "label", text = "Dump", text_size = 12, position = {10, 8}, size = {100, 32}, action = "onDump"},
        { type = "label", text = "Item", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {60, 18}, on_click = function()
            application.events:send("encyclopedia.open_item", item)
        end},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "view", position = {310, 30}, size = {80, 25}, background = 0x3e4b0b, views = {
            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00 },
            { type = "label", id = "lb_health", text = "80/120", text_size = 16, padding = 7 },
        }},

        -- Detailled informations
        { type = "list", id = "frame_content", position = {0, 60}, views = {

            -- Building
            { type = "list", id = "frame_build", position = {0, 60}, margin = {10, 0, 0, 10}, size = {300, 200}, views = {
                { type = "label", id = "lb_build_progress", text_size = 22, size = {400, 26}},
                { type = "image", id = "img_build_progress", src = "[base]/graphics/needbar.png", size = {380, 16}, texture_rect = {0, 0, 100, 16}},
                { type = "label", id = "lb_build_cost", text_size = 12, size = {400, 26}},

                { type = "list", id = "frame_components", size = {300, 100}, views = {
                    { type = "label", text = "Components", text_size = 20, margin = {10, 0, 0, 0}},
                    { type = "list", id = "list_components", position = {0, 10}},
                }},
            }},

            -- Workers
            { type = "list", id = "frame_workers", margin = {10, 0, 0, 10}, size = {300, 200}, views = {
                { type = "label", text = "Workers", text_size = 20},
                { type = "list", id = "list_workers"},
            }},

            -- Effects
            { type = "label", id = "lb_effect_oxygen", text_size = 18, padding = 10},

            -- Slots
            { type = "label", id = "lb_slots", text_size = 18, padding = 10},
            { type = "label", id = "lb_used_by", text_size = 18, padding = 10},

            -- Actions
            { type = "list", id = "frame_actions", margin = {10, 0, 0, 10}, views = {
                { type = "label", text = "Actions", text_size = 22, size = {400, 26}},
                { type = "list", id = "list_actions"},
            }},

            -- Factory progress
            { type = "list", id = "frame_factory", position = {0, 300}, margin = {40, 0, 0, 10}, views = {
--                { type = "label", text = "Work in progress", text_size = 22},
--                { type = "label", id = "lb_factory_status", text = "lb_factory_receipt", size = {100, 20}},
--                { type = "image", id = "img_factory_progress", src = "[base]/graphics/needbar.png", size = {352, 16}},

                -- Factory inventory
                { type = "list", id = "frame_factory_inventory", margin = {10, 0, 0, 10}, views = {
                    { type = "label", text = "Inventory", text_size = 18, size = {400, 26}},
                    { type = "list", id = "list_factory_inventory"},
                }},

                --                { type = "label", id = "lb_factory_receipt", text = "lb_factory_receipt", position = {0, 10}},
                --                { type = "label", id = "lb_factory_progress", text = "lb_factory_progress", position = {0, 20}},
                --                { type = "label", id = "lb_factory_character", text = "lb_factory_character", position = {0, 20}},
                --                { type = "label", id = "lb_factory_components", position = {0, 20}},
                --                { type = "label", id = "lb_factory_inputs", position = {0, 20}},
                --                { type = "label", id = "lb_factory_products", position = {0, 20}},
            }},

--            -- Factory orders
--            { type = "list", id = "frame_factory_orders", margin = {40, 0, 0, 10}, views = {
--                { type = "label", text = "Orders", text_size = 22},
--                { type = "list", id = "list_orders", position = {0, 8}},
--            }},
--
--            -- Networks
--            { type = "list", id = "frame_networks", margin = {60, 0, 0, 10}, views = {
--                { type = "label", text = "Networks", text_size = 22, size = {400, 26}},
--                { type = "list", id = "list_networks"},
--            }},

            -- Action buttons
            { type = "label", id = "bt_dump", text = "Dump", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 10, position = {10, 380}, size = {350, 32}, on_click = function()
                application:destroy(item)
            end},
            { type = "label", id = "bt_cancel", text = "Cancel", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 10, position = {10, 380}, size = {350, 32}, on_click = function()
                application:cancel(item)
            end},
        }},
    },

    --    on_event = function(view, event , data)
    --        if event == application.events.on_key_press and data == "ESCAPE" then
    --            view:setVisible(false)
    --            application.game:clearSelection();
    --            item = nil
    --        end
    --
    --        if event == application.events.on_deselect then
    --            view:setVisible(false)
    --            item = nil
    --        end
    --
    --        if event == application.events.on_item_selected then
    --            item = data;
    --            view:setVisible(true)
    --            view:findById("lb_name"):setText(item:getLabel())
    --
    --            display_actions_info(view, item)
    --
    --            if item:getInfo().factory and item:getInfo().factory.receipts then
    --                display_factory_info(view, item:getFactory(), item:getInfo().factory)
    --                view:findById("frame_factory_progress"):setVisible(true)
    --                view:findById("frame_factory_orders"):setVisible(true)
    --            else
    --                view:findById("frame_factory_progress"):setVisible(false)
    --                view:findById("frame_factory_orders"):setVisible(false)
    --            end
    --        end
    --    end,
    --
    --    on_refresh = function(view)
    --        if item ~= nil then
    --            view:findById("lb_health"):setText(item:getHealth() .. "/" .. item:getMaxHealth())
    --            view:findById("progress_health"):setSize(item:getHealth() / item:getMaxHealth() * 80, 25)
    --
    --            if item:getInfo().effects and item:getInfo().effects.oxygen > 0 then
    --                view:findById("lb_effect_oxygen"):setText("Oxygen", ": ", item:getInfo().effects.oxygen .. "(" .. item:getInfo().effects.pressure .. ")")
    --            end
    --
    --            if item:getNbFreeSlots() ~= -1 then
    --                view:findById("lb_slots"):setText("Slots", ":", item:getNbFreeSlots() .. "/" .. item:getSlots():size())
    --                view:findById("lb_slots"):setVisible(true)
    --            else
    --                view:findById("lb_slots"):setVisible(false)
    --            end
    --
    --            if item:getNbFreeSlots() < item:getNbSlots() then
    --                local str = ""
    --                local iterator = item:getSlots():iterator()
    --                while iterator:hasNext() do
    --                    local slot = iterator:next()
    --                    if slot:getJob() and slot:getJob():getCharacter() then
    --                        str = str .. (string.len(str) ~= 0 and ", " or "") .. slot:getJob():getCharacter():getName()
    --                    end
    --                end
    --                view:findById("lb_used_by"):setText("Used by", ": ", str)
    --                view:findById("lb_used_by"):setVisible(true)
    --            else
    --                view:findById("lb_used_by"):setVisible(false)
    --            end
    --
    --            if item:isComplete() then
    --                view:findById("frame_building"):setVisible(false)
    --            else
    --                view:findById("frame_building"):setVisible(true)
    --                view:findById("lb_building"):setDashedString("Building", item:getBuildProgress() > 0 and math.floor(item:getBuildProgress() * 100) or "waiting", 29)
    --                view:findById("img_building_progress"):setTextureRect(0, 80, math.floor(item:getBuildProgress() * 352 / 10) * 10, 16)
    --                view:findById("lb_building_progress"):setText("Progress: " .. item:getBuildProgress() .. "/" .. item:getTotalBuild())
    --                view:findById("lb_building_job"):setText("Build job: " .. (item:getBuildJob() and "yes" or "no"))
    --                view:findById("lb_building_character"):setText("Builder: " .. (item:getBuilder() and item:getBuilder():getName() or "no"))
    --                view:findById("list_building_components"):getAdapter():setData(item:getComponents());
    --            end
    --
    --            if item:getNetworkConnections() then
    --                local list_networks = view:findById("list_networks")
    --                list_networks:removeAllViews()
    --
    --                local isConnected = false
    --                local iterator = item:getNetworkConnections():iterator()
    --                while iterator:hasNext() do
    --                    local networkConnection = iterator:next()
    --                    if networkConnection:getNetwork() then
    --                        local lb_network = ui:createLabel()
    --                        lb_network:setDashedString(networkConnection:getNetwork():getInfo().label, math.floor(networkConnection:getNetwork():getQuantity()) .. "/" .. networkConnection:getNetwork():getMaxQuantity(), 48)
    --                        lb_network:setTextSize(14)
    --                        lb_network:setSize(400, 24)
    --                        list_networks:addView(lb_network)
    --                        isConnected = true
    --                    end
    --                end
    --
    --                view:findById("frame_networks"):setVisible(isConnected)
    --            else
    --                view:findById("frame_networks"):setVisible(false)
    --            end
    --
    --            if item:getFactory() then
    --                local factory = item:getFactory()
    --                local progress = factory:getJob() and factory:getJob():getProgress() or 0
    --
    --                --                view:findById("lb_factory_progress"):setText("Status", ": ", factory:getMessage())
    --                view:findById("img_factory_progress"):setTextureRect(0, 80, math.floor(progress * 352 / 10) * 10, 16)
    --
    --                if factory:getActiveReceipt() then
    --                    view:findById("lb_factory_status"):setDashedString(
    --                        factory:getActiveReceipt().receiptInfo.label, progress > 0 and math.floor(progress * 100) .. "%" or factory:getMessage(), 44)
    --                else
    --                    view:findById("lb_factory_status"):setText("None", " (", factory:getMessage():lower(), ")");
    --                end
    --
    --                --                view:findById("lb_factory_character"):setText("Crafter", ": ",
    --                --                    factory:getJob() and factory:getJob():getCharacter() and factory:getJob():getCharacter():getName() or "none")
    --                --
    --                --                if item:getFactory():getActiveReceipt() then
    --                --                    local str = "shopping list: "
    --                --                    local iterator = item:getFactory():getActiveReceipt():getShoppingList():iterator()
    --                --                    while iterator:hasNext() do
    --                --                        local product = iterator:next()
    --                --                        str = str .. product.consumable:getInfo().name .. " x" .. product.quantity
    --                --                    end
    --                --                    view:findById("lb_factory_components"):setText(str)
    --                --                else
    --                --                    view:findById("lb_factory_components"):setText("shopping list: none")
    --                --                end
    --                --
    --                --                if item:getFactory():getActiveReceipt() then
    --                --                    local str = "components: "
    --                --                    local iterator = item:getFactory():getActiveReceipt():getComponents():iterator()
    --                --                    while iterator:hasNext() do
    --                --                        local component = iterator:next()
    --                --                        str = str .. component.itemInfo.name .. " " .. component.currentQuantity .. "/" .. component.totalQuantity
    --                --                    end
    --                --                    view:findById("lb_factory_inputs"):setText(str)
    --                --                else
    --                --                    view:findById("lb_factory_inputs"):setText("components: none")
    --                --                end
    --            end
    --        end
    --    end
})

--function display_actions_info(view, item)
--    if item:getInfo().actions then
--        view:findById("frame_actions"):setVisible(true)
--
--        local list_actions = view:findById("list_actions")
--        list_actions:removeAllViews()
--
--        local iterator = item:getInfo().actions:iterator()
--        while iterator:hasNext() do
--            local action = iterator:next()
--
--            local view_action = ui:createView()
--            view_action:setSize(372, 42)
--            list_actions:addView(view_action)
--
--            local icon_action = ui:createLabel()
--            icon_action:setText("+")
--            icon_action:setTextSize(14)
--            icon_action:setPadding(0, 2)
--            icon_action:setSize(12, 12)
--            icon_action:setBackgroundColor(0x349394)
--            view_action:addView(icon_action)
--
--            local lb_action = ui:createLabel()
--            lb_action:setText(action.type)
--            lb_action:setTextSize(16)
--            lb_action:setPosition(20, 0)
--            lb_action:setSize(372, 24)
--            view_action:addView(lb_action)
--
--            local str = ""
--            local grid_effects = ui:createGrid()
--            grid_effects:setColumns(8)
--            grid_effects:setColumnWidth(80)
--            grid_effects:setRowHeight(24)
--            grid_effects:setPosition(0, 20)
--            add_effect_to_grid(grid_effects, "food", action.effects.food)
--            add_effect_to_grid(grid_effects, "drink", action.effects.drink)
--            add_effect_to_grid(grid_effects, "energy", action.effects.energy)
--            add_effect_to_grid(grid_effects, "happiness", action.effects.happiness)
--            add_effect_to_grid(grid_effects, "health", action.effects.health)
--            add_effect_to_grid(grid_effects, "relation", action.effects.relation)
--            add_effect_to_grid(grid_effects, "oxygen", action.effects.oxygen)
--            add_effect_to_grid(grid_effects, "entertainment", action.effects.entertainment)
--            view_action:addView(grid_effects)
--
--        end
--
--    else
--        view:findById("frame_actions"):setVisible(false)
--    end
--end
--
--function add_effect_to_grid(grid, label, value)
--    if value and value ~= 0 then
--        local lb_effects = ui:createLabel()
--        lb_effects:setText(label, ": ", (value > 0 and "+" or "-") .. value)
--        lb_effects:setTextSize(14)
--        lb_effects:setSize(400, 24)
--        grid:addView(lb_effects)
--    end
--end
--
--function display_factory_info(view, factory, factoryInfo)
--    local list = view:findById("list_orders")
--    list:removeAllViews()
--
--    local iterator = factory:getOrders():iterator()
--    while iterator:hasNext() do
--        local order = iterator:next()
--        local receipt_group = order.receiptGroupInfo
--
--        -- Create frame receipt detail
--        local frame_receipt_detail = ui:createList()
--        frame_receipt_detail:setSize(372, 100)
--        frame_receipt_detail:setVisible(false)
--        local iterator_receipt = receipt_group.receipts:iterator()
--        while iterator_receipt:hasNext() do
--            local receipt = iterator_receipt:next()
--
--            if receipt.inputs then
--                local str_inputs = ""
--                local iterator_inputs = receipt.inputs:iterator()
--                while iterator_inputs:hasNext() do
--                    local input = iterator_inputs:next()
--                    str_inputs = str_inputs .. (string.len(str_inputs) > 0 and " + " or "") .. input.quantity .. " " .. input.item.label
--                end
--
--                local lb_receipt = ui:createLabel()
--                lb_receipt:setSize(372, 22)
--                lb_receipt:setText(str_inputs)
--                lb_receipt:setTextSize(14)
--                frame_receipt_detail:addView(lb_receipt)
--            end
--        end
--
--        -- Create frame receipt
--        local frame_receipt = ui:createView()
--        frame_receipt:setSize(372, 22)
--
--        local lb_receipt_expend = ui:createLabel()
--        lb_receipt_expend:setSize(14, 14)
--        lb_receipt_expend:setText("+")
--        lb_receipt_expend:setTextSize(14)
--        lb_receipt_expend:setPadding(1, 4)
--        lb_receipt_expend:setBackgroundColor(0x329596)
--        lb_receipt_expend:setOnClickListener(function()
--            frame_receipt_detail:setVisible(not frame_receipt_detail:isVisible())
--            lb_receipt_expend:setText(frame_receipt_detail:isVisible() and "-" or "+");
--        end)
--        frame_receipt:addView(lb_receipt_expend)
--
--        local lb_receipt_group = ui:createLabel()
--        lb_receipt_group:setSize(372, 32)
--        lb_receipt_group:setPosition(20, 0)
--        lb_receipt_group:setPadding(3)
--        lb_receipt_group:setText(receipt_group.label)
--        frame_receipt:addView(lb_receipt_group)
--
--        local lb_mode = ui:createLabel()
--        lb_mode:setText("mode")
--        lb_mode:setSize(50, 22)
--        lb_mode:setPosition(280, 0)
--        lb_mode:setPadding(3)
--        lb_mode:setOnClickListener(function(v)
--            lb_mode:setText("gg")
--        end)
--        frame_receipt:addView(lb_mode)
--
--        local lb_up = ui:createLabel()
--        lb_up:setText("U")
--        lb_up:setSize(50, 22)
--        lb_up:setPosition(240, 0)
--        lb_up:setPadding(3)
--        lb_up:setOnClickListener(function(v)
--            factory:moveReceipt(receipt_group, -1)
--            display_factory_info(view, factory, factoryInfo)
--        end)
--        frame_receipt:addView(lb_up)
--
--        local lb_down = ui:createLabel()
--        lb_down:setText("D")
--        lb_down:setSize(50, 22)
--        lb_down:setPosition(260, 0)
--        lb_down:setPadding(3)
--        lb_down:setOnClickListener(function(v)
--            factory:moveReceipt(receipt_group, 1)
--            display_factory_info(view, factory, factoryInfo)
--        end)
--        frame_receipt:addView(lb_down)
--
--        local lb_active = ui:createLabel()
--        lb_active:setText(order.isActive and "[x]" or "[ ]")
--        lb_active:setSize(50, 22)
--        lb_active:setPosition(326, 0)
--        lb_active:setPadding(3)
--        lb_active:setOnClickListener(function(v)
--            receiptGroup.isActive = not receiptGroup.isActive
--            display_factory_info(view, factory, factoryInfo)
--        end)
--        frame_receipt:addView(lb_active)
--
--        list:addView(frame_receipt)
--        list:addView(frame_receipt_detail)
--    end
--end