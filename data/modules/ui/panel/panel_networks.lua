data:extend({
    type = "view",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    id = "panel_networks",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}, on_click = function()
            game.ui:findById("panel_main"):setVisible(true)
            game.ui:findById("panel_networks"):setVisible(false)
        end},
        { type = "label", text = "Networks", text_size = 28, padding = 10, position = {46, 0}},
        { type = "list", id = "list_networks", position = {10, 40}},
    },
    on_event = function(view, event , data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:findById("panel_main"):setVisible(true)
        end
    end,

    on_refresh = function(view)
        local list = view:findById("list_networks")
        list:removeAllViews()

        local module = game:getModule("NetworkModule")
        local iterator = module:getNetworks():iterator()
        while iterator:hasNext() do
            local network = iterator:next()
            local lb_network = game.ui:createLabel()
            lb_network:setSize(400, 20)
            --                lb_network:setText(network:getInfo().label)
            lb_network:setDashedString(network:getInfo().label, network:getSize(), 47)
            list:addView(lb_network)

            local lb_network_detail = game.ui:createLabel()
            lb_network_detail:setSize(400, 20)
            lb_network_detail:setText("Quantity", ": ", network:getQuantity() .. "/" .. network:getMaxQuantity())
            list:addView(lb_network_detail)
        end
    end
})