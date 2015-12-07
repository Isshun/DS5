room = nil

data:extend({
    type = "view",
    name = "info_room",
    position = {0, 620},
    size = {372, 280},
    background = 0x121c1e,
    visible = true,
    views = {
        { type = "list", position = {10, 10}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 16, size = {100, 28}},
            { type = "label", id = "lb_id", text_size = 12, size = {100, 18}},
            { type = "label", id = "lb_size", text_size = 12, size = {100, 18}},
            { type = "label", id = "lb_exterior", text_size = 12, size = {100, 18}},
            { type = "label", id = "lb_temperature", text_size = 12, size = {100, 18}},
            { type = "label", id = "lb_oxygen", text_size = 12, size = {100, 18}},
            { type = "label", id = "lb_target_oxygen", text_size = 12, size = {100, 18}},
            { type = "label", id = "lb_neighborhood", text_size = 12, text = "Connections:", size = {100, 18}},
            { type = "grid", id = "grid_neighborhood", columns = 3, column_width = 132, row_height = 16, size = {100, 18}},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_parcel_over then
            room = data and data:getRoom() or nil;
        end

        if event == application.events.on_display_change and data[1] == "debug" then
            view:setVisible(data[2])
        end
    end,

    on_refresh = function(view)
        if room ~= nil then
            view:findById("lb_name"):setText(room:getName())
            view:findById("lb_id"):setText("Id", ": ", room:getId())
            view:findById("lb_size"):setText("Size", ": ", room:getSize())
            view:findById("lb_exterior"):setText("Exterior", ": ", room:isExterior() and "yes" or "no")
            view:findById("lb_temperature"):setText("Temp", ": ", room:getTemperature() .. "")
            view:findById("lb_oxygen"):setText("O2", ": ", room:getOxygen() .. "")
            view:findById("lb_target_oxygen"):setText("Target O2", ": ", room:getTargetOxygen() .. " (pressure: " .. room:getTargetOxygenPressure() .. ")")

            view:findById("grid_neighborhood"):removeAllViews();
            if room:getConnections() then
                local iterator = room:getConnections():iterator()
                while iterator:hasNext() do
                    local neighbor = iterator:next()
                    local lb_connection = ui:createLabel()
                    lb_connection:setTextSize(12)
                    lb_connection:setText(neighbor:getRoom():getName() .. " (" .. neighbor:getPermeability() .. "/" .. neighbor:getBorderSize() .. ")")
                    view:findById("grid_neighborhood"):addView(lb_connection)
                end
            end
        else
            view:findById("lb_name"):setText("None")
            view:findById("lb_id"):setText(" ")
            view:findById("lb_size"):setText(" ")
            view:findById("lb_exterior"):setText(" ")
            view:findById("grid_neighborhood"):removeAllViews();
        end
    end
})