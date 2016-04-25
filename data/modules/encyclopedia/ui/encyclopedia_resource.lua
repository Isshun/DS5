data:extend(
    {
        {
            type = "view",
            name = "encyclopedia-resource",
            position = {400, 200},
            size = {800, 600},
            background = 0x62bcbe,
            visible = false,
            views = {
                type = "view",
                position = {1, 1},
                size = {798, 598},
                background = 0x121c1e,
                views = {
                    { type = "label", id = "lb_name", text = "name", text_size = 32, padding = 18},
                    { type = "label", id = "lb_content", text = "name", text_size = 16, position = {0, 40}, padding = 18},
                    { type = "label", id = "bt_close", text = "[close]", text_size = 22, position = {650, 10}, background = 0x885566, size = {80, 32}, padding = 10, on_click = function()
                        application.events:send("encyclopedia.close")
                    end},

                    { type = "view", id = "view_plant", visible = false, position = {10, 54}, views = {
                        { type = "label", text = "Growing states", text_size = 24, padding = 10},
                        { type = "list", id = "list_plant", position = {10, 45} },
                    }}
                }
            },
            on_event =
            function(view, event, data)
                if event == application.events.on_key_press and data == "ESCAPE" then
                    view:setVisible(false)
                end

                if event == "encyclopedia.close" then
                    view:setVisible(false)
                end

                if event == "encyclopedia.open" then
                    view:setVisible(true)
                    view:findById("view_plant"):setVisible(false)
                    view:findById("lb_name"):setText(data[1])
                    view:findById("lb_content"):setText(data[2])
                end

                if event == "encyclopedia.open_resource" then
                    local info = data:getInfo()

                    view:setVisible(true)
                    view:findById("lb_name"):setText(info.label)

                    view:findById("view_plant"):setVisible(info.isPlant)
                    if info.isPlant then
                        local listPlant = view:findById("list_plant")
                        listPlant:removeAllViews()
                        local iterator = info.plant.states:iterator()
                        while iterator:hasNext() do
                            local state = iterator:next()
                            local viewState = ui:createView()
                            viewState:setSize(600, 24)

                            local labelState = ui:createLabel()
                            labelState:setText(state.name)
                            labelState:setTextSize(16)
                            labelState:setSize(200, 24)
                            labelState:setPosition(0, 0)
                            viewState:addView(labelState)

                            local labelState = ui:createLabel()
                            labelState:setText("Light: " .. state.light[1] .. " to " .. state.light[2])
                            labelState:setTextSize(16)
                            labelState:setSize(200, 24)
                            labelState:setPosition(200, 0)
                            viewState:addView(labelState)

                            local labelState = ui:createLabel()
                            labelState:setText("Temperature: " .. state.temperature[1] .. " to " .. state.temperature[2])
                            labelState:setTextSize(16)
                            labelState:setSize(200, 24)
                            labelState:setPosition(400, 0)
                            viewState:addView(labelState)

                            listPlant:addView(viewState)
                        end
                    end
                end
            end,
        },
    }
)