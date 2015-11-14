room = nil

data:extend({
    type = "view",
    name = "info_room",
    position = {800, 820},
    size = {400, 800},
    background = 0x121c1e,
    visible = true,
    views = {
        { type = "list", position = {10, 10}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 22, size = {100, 30}},
            { type = "label", id = "lb_id", text_size = 14},
            { type = "label", id = "lb_size", text_size = 14},
            { type = "label", id = "lb_exterior", text_size = 14},
            { type = "label", id = "lb_neighborhood", text_size = 14},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_parcel_over then
            room = data and data:getRoom() or nil;
            view:setVisible(true)
        end
    end,

    on_refresh = function(view)
        if room ~= nil then
            view:findById("lb_name"):setText(room:getName())
            view:findById("lb_id"):setText("Id", ": ", room:getId())
            view:findById("lb_size"):setText("Size", ": ", room:getSize())
            view:findById("lb_exterior"):setText("Exterior", ": ", room:isExterior() and "yes" or "no")

            if room:getNeighbors() then
                local str = ""
                local iterator = room:getNeighbors():iterator()
                while iterator:hasNext() do
                    local neighbor = iterator:next()
                    str = str .. neighbor:getRoom():getName() .. "(" .. neighbor:getBorderValue() .. "," .. neighbor:getBorderSize() .. ")"
                end
                view:findById("lb_neighborhood"):setText("Neighborhood", ": ", str)
            end
        else
            view:findById("lb_name"):setText("None")
            view:findById("lb_id"):setText(" ")
            view:findById("lb_size"):setText(" ")
            view:findById("lb_exterior"):setText(" ")
            view:findById("lb_neighborhood"):setText(" ")
        end
    end
})