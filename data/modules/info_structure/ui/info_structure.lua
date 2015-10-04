structure = nil

game.data:extend(
{
    {
        type = "list",
        name = "ui-test",
        position = {1200, 65},
        size = {400, 800},
        background = 0x121c1e,
        visible = false,
        views =
        {
            { type = "label", id = "lb_name", text = "lb_name", text_size = 28, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_durability", text = "lb_durability", text_size = 16, position = {10, 10}, size = {-1, 32}},
        },
        
        on_load =
            function(view)
                mode = 3
            end,
        
        on_event =
            function(event, view, data)
                if event == game.events.on_deselect then
                    view:setVisible(false)
                    structure = nil
                end

                if event == game.events.on_structure_selected then
                    view:setVisible(true)
                    structure = data;
                    
                    view:findById("lb_name"):setText(structure:getLabel())
                end
            end,
            
        on_refresh =
            function(view)
                if structure ~= nil then
                    view:findById("lb_durability"):setText(structure:getHealth() .. "/" .. structure:getMaxHealth())
                end
            end
    },
}
)