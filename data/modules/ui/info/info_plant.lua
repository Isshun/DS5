plant = nil

data:extend({
    type = "view",
    name = "base.ui.info_plant",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "label", text = "Plant", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "list", position = {10, 40}, views = {
            { type = "label", id = "lb_job", text_size = 18},
            { type = "label", id = "lb_quantity", text_size = 18},
            { type = "label", id = "lb_maturity", text_size = 18},
            { type = "label", id = "lb_grow_state", text_size = 18},
            { type = "label", id = "lb_tile", text_size = 18},
            { type = "label", id = "lb_seed", text_size = 18},
            { type = "label", id = "lb_nourish", text_size = 18},
        }},
        { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 30}, size = {90, 40}, on_click = function()
            application.events:send("encyclopedia.open_resource", plant)
        end},
    },

    on_event = function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.game:clearSelection();
            plant = nil
        end

        if event == application.events.on_deselect then
            view:setVisible(false)
            plant = nil
        end

        if event == application.events.on_plant_selected then
            view:setVisible(true)
            view:findById("lb_name"):setText(data:getLabel())
            plant = data;
        end
    end,

    on_refresh = function(view)
        if plant ~= nil then
            view:findById("lb_tile"):setText("Tile: " .. plant:getTile())
            view:findById("lb_seed"):setText("Seed", ": ", plant:hasSeed() and "yes" or "no")
            view:findById("lb_nourish"):setText("Nourish", ": ", math.floor(plant:getNourish() * 100) .. "%")
            view:findById("lb_maturity"):setText("Maturity", ": ", math.floor(plant:getMaturity() * 100) .. "%")
            view:findById("lb_maturity"):setVisible(true)

            if plant:getJob() then
                view:findById("lb_job"):setText("Job: " .. plant:getJob():getLabel())
            end

            local growState = plant:getGrowState()
            if growState then
                view:findById("lb_grow_state"):setText("Grow state", ": ", growState.name, " (" .. (growState.value * 100) .. "%)")
                view:findById("lb_grow_state"):setVisible(true)
            end
        end
    end
})