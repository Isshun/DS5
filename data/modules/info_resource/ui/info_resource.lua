resource = nil

game.data:extend(
{
    {
        type = "view",
        name = "ui-test",
        position = {1200, 65},
        size = {400, 800},
        background = 0x121c1e,
        visible = false,
        views =
        {
            { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10},
            
            { type = "list", position = {0, 40}, views = {
                { type = "label", id = "lb_position", text_size = 18, padding = 10},
                { type = "label", id = "lb_quantity", text_size = 18, padding = 10},
            }},
        },
        
        on_event =
            function(event, view, data)
                if event == game.events.on_deselect then
                    view:setVisible(false)
                    resource = nil
                end

                if event == game.events.on_resource_selected then
                    view:setVisible(true)
                    view:findById("lb_name"):setText(data:getLabel())
                    resource = data;
                end
            end,
            
        on_refresh =
            function(view)
                if resource ~= nil then
                    view:findById("lb_position"):setText("Position: " .. resource:getX() .. "x" .. resource:getY())
                    view:findById("lb_quantity"):setText("Quantity: " .. resource:getQuantity())
                end
            end
    },
}
)