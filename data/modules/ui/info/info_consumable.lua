consumable = nil

data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views =
    {
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "label", text = "Consumable", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "list", position = {10, 70}, views = {
            { type = "label", id = "lb_quantity", text_size = 16},
            { type = "label", id = "lb_haul", text_size = 16},
            { type = "label", id = "lb_storage_area", text_size = 16},
        }},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
            application.events:send("encyclopedia.open_consumable", consumable)
        end},
    },

    on_event =
    function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.ui:clearSelection();
            consumable = nil
        end

        if event == application.events.on_deselect then
            view:setVisible(false)
            consumable = nil
        end

        if event == application.events.on_consumable_selected then
            view:setVisible(true)
            view:findById("lb_name"):setText(data:getLabel())
            consumable = data;
        end
    end,

    on_refresh =
    function(view)
        if consumable ~= nil then
            view:findById("lb_quantity"):setText("Quantity: " .. consumable:getQuantity())

            local store_job = consumable:getStoreJob()
            if store_job then
                view:findById("lb_haul"):setVisible(true)
                view:findById("lb_haul"):setText("Haul: " .. (store_job:getMessage() and store_job:getMessage() or "no message"))
            else
                view:findById("lb_haul"):setVisible(false)
            end

            if consumable:getStorage() then
                view:findById("lb_storage_area"):setVisible(true)
                view:findById("lb_storage_area"):setText("Storage: " .. consumable:getStorage():getName())
            else
                view:findById("lb_storage_area"):setVisible(false)
            end
        end
    end
})