data:extend({
    type = "view",
    name = "info_parcel",
    position = {200, 10},
    size = {200, 600},
    background = 0x121c1e,
    visible = true,
    views = {
        { type = "list", id = "list_consumables", position = {10, 40}}
    },

    on_refresh = function(view)
        local consumables = {}
        local iterator = game.world:getConsumables():iterator()
        while iterator:hasNext() do
            local consumable = iterator:next()
            if not consumables[consumable:getInfo()] then
                consumables[consumable:getInfo()] = consumable:getQuantity()
            else
                consumables[consumable:getInfo()] = consumables[consumable:getInfo()] + consumable:getQuantity()
            end
        end
        table.sort(consumables, function(a,b)
            return a[1] < b[1]
        end)

        local list_consumables = view:findById("list_consumables")
        list_consumables:removeAllViews()
        for key, value in pairs(consumables) do
            local lb_consumable = game.ui:createLabel()
            lb_consumable:setDashedString(key.label, value, 22);
            lb_consumable:setSize(200, 24)
            list_consumables:addView(lb_consumable)
        end
    end
})