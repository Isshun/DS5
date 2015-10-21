data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 65},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
        { type = "label", id = "lb_label", text = "name", text_size = 28, padding = 10, size = {100, 40}},
    },

    on_event =
    function(event, view, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:clearSelection();
        end

        if event == game.events.on_deselect then
            view:setVisible(false)
        end

        if event == game.events.on_receipt_selected then
            view:setVisible(true)
            view:findById("lb_name"):setText(data.name)
            view:findById("lb_label"):setText(data.label)
        end
    end,
})