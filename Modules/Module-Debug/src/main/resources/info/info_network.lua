network = nil

ui:extend({
    type = "view",
    name = "base.ui.info_network",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    visible = false,
    views =
    {
        { type = "label", text = "Network", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "list", position = {0, 60}, views = {
            { type = "label", id = "lb_durability", text = "lb_durability", text_size = 16, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_complete", text = "lb_complete", text_size = 16, position = {10, 10}, size = {-1, 32}},

            -- Building info
            { type = "list", id = "frame_building", position = {0, 40}, views = {
                { type = "label", text = "Building in progress", text_size = 22, padding = 10, size = {400, 26}},
                { type = "label", id = "lb_building_progress", text_size = 14, padding = 10},
                { type = "label", id = "lb_building_job", text_size = 14, padding = 10},
                { type = "label", id = "lb_building_character", text_size = 14, padding = 10},
                { type = "label", id = "lb_building_all_componment", text_size = 14, padding = 10},
                { type = "label", text = "Components", text_size = 20, padding = 10, position = {0, 5}},
                { type = "list", id = "list_building_components", position = {0, 10}, adapter = {
                    view = { type = "label", text_size = 14, padding = 10 },
                    on_bind = function(view, data)
                        print (data.info)
                        local left = data.info.label .. " (" .. data.currentQuantity .. "/" .. data.neededQuantity .. ")"
                        local right = (data.job and (data.job:getCharacter() and data.job:getCharacter():getName() or (data.currentQuantity < data.neededQuantity and "waiting" or "complete")) or "no job")
                        view:setDashedString(left, right, 48)
                    end
                }},
            }},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.game:clearSelection();
            network = nil
        end

        if event == application.events.on_deselect then
            view:setVisible(false)
            network = nil
        end

        if event == application.events.on_network_selected then
            view:setVisible(true)
            network = data;

            view:findById("lb_name"):setText(network:getLabel())
        end
    end,

    on_refresh = function(view)
        if network ~= nil then
            view:findById("lb_durability"):setText(network:getHealth() .. "/" .. network:getMaxHealth())
            view:findById("lb_complete"):setText("Complete: " .. (network:isComplete() and "yes" or "no"))

            view:findById("frame_building"):setVisible(not network:isComplete())
            if not network:isComplete() then
--                view:findById("lb_building_progress"):setText("Progress: " .. network:getCurrentBuild() .. "/" .. network:getTotalBuild())
--                view:findById("lb_building_job"):setText("Build job: " .. (network:getBuildJob() and "yes" or "no"))
--                view:findById("lb_building_character"):setText("Builder: " .. (network:getBuilder() and network:getBuilder():getName() or "no"))
--                view:findById("lb_building_all_componment"):setText("All components: " .. (network:hasAllComponents() and "yes" or "no"))
--                view:findById("list_building_components"):getAdapter():setData(network:getComponents());
            end

        end
    end
})