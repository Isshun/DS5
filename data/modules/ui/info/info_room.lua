room = nil

data:extend({
    type = "view",
    name = "info_room",
    position = {800, 820},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "list", position = {10, 40}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 22, padding = 5, size = {100, 30}},
            { type = "label", id = "lb_id", text_size = 14, padding = 5},
            { type = "label", id = "lb_size", text_size = 14, padding = 5},
            { type = "label", id = "lb_neighborhood", text_size = 14, padding = 5},
        }},
    },

    on_event =
    function(view, event, data)
        if event == game.events.on_parcel_over then
            room = data and data:getRoom() or nil;
        end
    end,

    on_refresh =
    function(view)
        if room ~= nil then
            view:setVisible(true)
            view:findById("lb_name"):setText(room:getName())
            view:findById("lb_id"):setText("Id", ": ", room:getId())
            view:findById("lb_size"):setText("Size", ": ", room:getSize())

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
            view:setVisible(false)
        end
    end
})