--local function display_accepted_items(view, garden)
--    local list = view:findById("list_accepted_plant")
--    list:removeAllViews()
--
--    local iterator = garden:getPotentials():iterator()
--    while iterator:hasNext() do
--        local plant = iterator:next()
--        local lb_plant = ui:createLabel()
--        lb_plant:setText((garden:getCurrent() == plant and "[x] " or "[ ] "), plant.label)
--        lb_plant:setTextSize(14)
--        lb_plant:setSize(180, 20)
--        lb_plant:setPadding(5)
--        lb_plant:setOnClickListener(function()
--            garden:setAccept(plant, true)
--            display_accepted_items(view, garden)
--        end)
--        list:addView(lb_plant)
--    end
--end

data:extend({
    type = "list",
    id = "base.ui.info_area_garden",
    style = "base.style.right_panel",
    visible = false,
    views = {
        { type = "view", size = {400, 40}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 45}, position = {0, 8}},
            { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {90, 32}, on_click = function()
                application.events:send("encyclopedia.open_area", g_area)
            end},
        }},
        { type = "label", text = "Farming", text_size = 22, padding = 10, size = {100, 45}, position = {0, 8}},
        { type = "list", id = "list_accepted_plant", position = {5, 3}},
    },

--    on_event = function(view, event, data)
--        if event == application.events.on_key_press and data == "ESCAPE" then
--            view:setVisible(false)
--            application.game:clearSelection();
--        end
--
--        if event == application.events.on_deselect then
--            view:setVisible(false)
--        end
--
--        if event == application.events.on_area_selected and data:getTypeName() == "GARDEN" then
--            display_accepted_items(view, data)
--            view:setVisible(true)
--            view:findById("lb_name"):setText(data:getName())
--        end
--    end,
})