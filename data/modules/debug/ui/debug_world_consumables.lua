data:extend({
    type = "view",
    name = "base.ui.debug_world_consumables",
    position = {200, 20},
    size = {200, 600},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", text = "Consomables", text_size = 16, padding = 10, size = {200, 30}, background = 0x333333},
        { type = "list", id = "list_consumables", position = {10, 40}},
    },

    on_refresh = function(view)
        local consumables = {}
        local iterator = application.world:getConsumables():iterator()
        while iterator:hasNext() do
            local consumable = iterator:next()
            local info = consumable:getInfo()
            consumables[info] = (consumables[info] and consumables[info] or 0) + consumable:getQuantity()
        end
        table.sort(consumables, function(a,b)
            return a[1] < b[1]
        end)

        local list_consumables = view:findById("list_consumables")
        list_consumables:removeAllViews()
        for key, value in pairs(consumables) do
            local lb_consumable = ui:createLabel()
            lb_consumable:setDashedString(key.label, value, 22);
            lb_consumable:setSize(200, 24)
            list_consumables:addView(lb_consumable)
        end
    end,

    on_event =
    function(view, event, data)
--        if event == application.events.on_key_press and data == "F2" then
--            view:setVisible(not view:isVisible())
--        end
    end,

})