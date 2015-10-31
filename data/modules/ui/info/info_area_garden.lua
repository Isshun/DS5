g_garden = nil
g_area_garden_refresh = nil

data:extend({
    type = "list",
    id = "info_area",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, padding = 10, size = {100, 40}},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
            game.events:send("encyclopedia.open_area", g_area)
        end},
        { type = "list", id = "frame_inventory", position = {0, 40}, views = {
            { type = "label", text = "Inventory", text_size = 18, padding = 10},
            { type = "list", id = "list_accepted_plant", position = {5, 75}},
        }},
    },

    on_event =
    function(event, view, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            g_garden = nil
            view:setVisible(false)
            game.ui:clearSelection();
        end

        if event == game.events.on_deselect then
            g_garden = nil
            view:setVisible(false)
        end

        if event == game.events.on_area_selected and data:getTypeName() == "GARDEN" then
            g_garden = data;
            g_area_garden_refresh = true
            view:setVisible(true)
            view:findById("lb_name"):setText(data:getName())
        end
    end,

    on_refresh =
    function(view)
        if g_garden and g_area_garden_refresh then
            g_area_garden_refresh = false

            local list = view:findById("list_accepted_plant")
            list:removeAllViews()
--            list:keepSorted(true)

            local list_item = {}
            local iterator = g_garden:getItemsAccepts():entrySet():iterator()
            while iterator:hasNext() do
                table.insert(list_item, iterator:next())
            end

            for key in pairs(list_item) do
                local entry = list_item[key]
                local lb_entry = game.ui:createLabel()
                lb_entry:setText((entry:getValue() and "[x] " or "[ ] "), entry:getKey().label)
                lb_entry:setTextSize(14)
                lb_entry:setSize(180, 20)
                lb_entry:setPadding(5)
                lb_entry:setOnClickListener(function(subview)
                    g_garden:setAccept(entry:getKey(), not entry:getValue())
                    g_area_garden_refresh = true
                end)
                list:addView(lb_entry)
            end
        end

    end
})