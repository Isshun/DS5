--ui:extend({
--    type = "list",
--    id = "panel_test",
--    position = {1200, 65},
--    size = {400, 800},
--    background = blue_dark_4,
--    visible = false,
--    views = {
--        { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
--    },
--    on_game_start = function()
--        local bt_test = ui:createLabel()
--        bt_test:setText("Test")
--        bt_test:setTextSize(18)
--        bt_test:setSize(170, 40)
--        bt_test:setBackgroundColor(0x349394ff)
--        bt_test:setPadding(10)
--        bt_test:setOnClickListener(function()
--            ui:find("base.ui.right_panel"):setVisible(false)
--            ui:find("panel_test"):setVisible(true)
--        end)
--        ui:find("base.ui.right_panel"):findById("main_grid"):addView(bt_test)
--    end
--})