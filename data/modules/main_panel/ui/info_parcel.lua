parcel = nil

game.data:extend(
    {
        {
            type = "view",
            name = "info_parcel",
            position = {1200, 820},
            size = {400, 800},
            background = 0x121c1e,
            visible = false,
            views =
            {
                { type = "list", position = {10, 40}, views = {
                    { type = "label", id = "lb_name", text = "name", text_size = 22, padding = 5, size = {100, 30}},
                    { type = "label", id = "lb_position", text_size = 14, padding = 5},
                    { type = "label", id = "lb_light", text_size = 14, padding = 5},
                    { type = "label", id = "lb_oxygen", text_size = 14, padding = 5},
                    { type = "label", id = "lb_room", text_size = 14, padding = 5},
                    { type = "label", id = "lb_type", text_size = 14, padding = 5},
                }},
            },

            on_event =
            function(event, view, data)
--                if event == game.events.on_key_press and data == "ESCAPE" then
--                    view:setVisible(false)
--                    game.ui:clearSelection();
--                    resource = nil
--                end
--
--                if event == game.events.on_deselect then
--                    view:setVisible(false)
--                    resource = nil
--                end

                if event == game.events.on_parcel_over then
                    parcel = data;
                    view:setVisible(true)
                end
            end,

            on_refresh =
            function(view)
                if parcel ~= nil then
                    local room = parcel:getRoom()
                    view:findById("lb_name"):setText("Ground")
                    view:findById("lb_position"):setText("Position: " .. parcel.x .. "x" .. parcel.y)
                    view:findById("lb_light"):setText("Light: " .. parcel:getLight())
                    view:findById("lb_oxygen"):setText("Oxygen: " .. parcel:getOxygen())
                    view:findById("lb_room"):setText("Room: " .. (room and (room:isExterior() and "exterior" or room:getType())))
                    view:findById("lb_type"):setText("Type: " .. parcel:getType())
                end
            end
        },
    }
)