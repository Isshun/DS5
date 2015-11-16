data:extend({
    type = "list",
    id = "panel_test",
    position = {1200, 65},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
    },
    on_load = function()
        local bt_test = application.ui:createLabel()
        bt_test:setText("Test")
        bt_test:setTextSize(18)
        bt_test:setSize(170, 40)
        bt_test:setBackgroundColor(0x349394)
        bt_test:setPadding(10)
        bt_test:setOnClickListener(function()
            application.ui:findById("panel_main"):setVisible(false)
            application.ui:findById("panel_test"):setVisible(true)
        end)
        application.ui:findById("panel_main"):findById("main_grid"):addView(bt_test)
    end
})