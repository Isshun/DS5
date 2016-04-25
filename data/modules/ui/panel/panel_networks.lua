data:extend({
    type = "view",
    style = "base.style.right_panel",
    id = "panel_networks",
    visible = false,
    views = {
        { type = "label", text = " < ", text_size = 34, position = {0, 7}, size = {32, 400}, on_click = function()
            ui:find("base.ui.panel_main"):setVisible(true)
            ui:find("panel_networks"):setVisible(false)
        end},
        { type = "label", text = "Networks", text_size = 28, padding = 10, position = {46, 0}},
        { type = "list", id = "list_networks", position = {10, 40}},
    },
    on_event = function(view, event , data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            ui:find("base.ui.panel_main"):setVisible(true)
            application:sendEvent("mini_map.display", true)
        end
    end,

    on_refresh = function(view)
        local list = view:findById("list_networks")
        list:removeAllViews()

        local module = application:getModule("NetworkModule")
        local iterator = module:getNetworks():iterator()
        while iterator:hasNext() do
            local network = iterator:next()
            local lb_network = ui:createLabel()
            lb_network:setSize(400, 20)
            --                lb_network:setText(network:getInfo().label)
            lb_network:setDashedString(network:getInfo().label, math.floor(network:getQuantity()) .. "/" .. network:getMaxQuantity(), 48)
            list:addView(lb_network)

--            local lb_network_detail = ui:createLabel()
--            lb_network_detail:setSize(400, 20)
--            lb_network_detail:setText("Quantity", ": ", )
--            list:addView(lb_network_detail)
        end
    end
})