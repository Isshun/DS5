rock = nil

ui:extend({
    type = "view",
    name = "base.ui.info_rock",
    style = "base.style.right_panel",
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "label", text = "Rock", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "list", position = {10, 40}, views = {
            { type = "label", id = "lb_job", text_size = 18},
            { type = "label", id = "lb_quantity", text_size = 18},
        }},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 30}, size = {90, 40}, on_click = function()
            application.events:send("encyclopedia.open_resource", rock)
        end},
    },

    on_event = function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.game:clearSelection();
            rock = nil
        end

        if event == application.events.on_deselect then
            view:setVisible(false)
            rock = nil
        end

        if event == application.events.on_rock_selected then
            view:setVisible(true)
            view:findById("lb_name"):setText(data.label)
            ui:find("base.ui.panel_main"):setVisible(false)
            rock = data;
        end
    end,

    on_refresh = function(view)
        if rock ~= nil then
--            view:findById("lb_tile"):setText("Tile: " .. rock:getTile())
--            view:findById("lb_quantity"):setText("Tile: " .. rock:getTile())
--
--            if rock:getJob() then
--                view:findById("lb_job"):setText("Job: " .. rock:getJob():getLabel())
--            end
        end
    end
})