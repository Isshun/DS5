parcel = nil

data:extend({
    type = "view",
    name = "info_parcel",
    position = {1200, 820},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views =
    {
        { type = "list", position = {10, 10}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 22, size = {100, 30}},
            { type = "label", id = "lb_teperature", text_size = 14},
            { type = "label", id = "lb_position", text_size = 14},
            { type = "label", id = "lb_connections", text_size = 14},
            { type = "label", id = "lb_room", text_size = 14},
            { type = "label", id = "lb_walkable", text_size = 14},
            { type = "label", id = "lb_light", text_size = 14},
            { type = "label", id = "lb_oxygen", text_size = 14},
            { type = "label", id = "lb_room", text_size = 14},
            { type = "label", id = "lb_type", text_size = 14},
        }},
    },

    on_event =
    function(view, event, data)
        if event == application.events.on_parcel_over then
            parcel = data;
            view:setVisible(true)
        end
    end,

    on_refresh =
    function(view)
        if parcel ~= nil then
            local room = parcel:getRoom()
            view:findById("lb_name"):setText("Ground")
            view:findById("lb_teperature"):setText("Temperature", ": ", parcel:getTemperature())
            view:findById("lb_position"):setText("Position", ": ", parcel.x .. "x" .. parcel.y .. "x" .. parcel.z)
            view:findById("lb_room"):setText("Room", ": ", parcel:getRoom() and parcel:getRoom():getName() or "no")
            view:findById("lb_light"):setText("Light", ": ", parcel:getLight())
            view:findById("lb_oxygen"):setText("Oxygen", ": ", parcel:getOxygen())
            view:findById("lb_room"):setText("Room", ": ", (room and (room:isExterior() and "exterior" or room:getType():name()) or "no"))
            view:findById("lb_walkable"):setText("Walkable", ": ", (parcel:isWalkable() and "yes" or "no"))

            if parcel:getConnections() then
                local str = "C: "
                local iterator = parcel:getConnections():iterator()
                while iterator:hasNext() do
                    local to_node = iterator:next():getToNode()
                    str = str .. to_node.x .. "x" .. to_node.y .. "x" .. to_node.z .. " "
                end
                view:findById("lb_connections"):setText(str)
            else
                view:findById("lb_connections"):setText("Connections: none")
            end
        end
    end
})