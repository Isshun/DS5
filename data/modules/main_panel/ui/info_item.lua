item = nil

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
            { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
            { type = "label", text = "Item", text_size = 12, position = {10, 8}},
            { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
            { type = "list", position = {0, 60}, views = {
                { type = "label", id = "lb_position", text_size = 18, padding = 10},
                { type = "list", id = "frame_building", position = {0, 40}, views = {
                    { type = "label", text = "Building in progress", text_size = 22, padding = 10, size = {400, 26}},
                    { type = "label", id = "lb_building_progress", text_size = 14, padding = 10},
                    { type = "label", id = "lb_building_job", text_size = 14, padding = 10},
                    { type = "label", id = "lb_building_character", text_size = 14, padding = 10},
                    { type = "label", text = "Components", text_size = 20, padding = 10, position = {0, 5}},
                    { type = "list", id = "list_building_components", position = {0, 10}, adapter = {
                        view = { type = "label", text_size = 14, padding = 10 },
                        on_bind = function(view, data)
                            view:setDashedString(data.info.label .. " (" .. data.currentQuantity .. "/" .. data.neededQuantity .. ")", (data.job and (data.job:getCharacter() and data.job:getCharacter():getName() or (data.currentQuantity < data.neededQuantity and "waiting" or "complete")) or "no job"), 48)
                        end
                    }},
                }},
            }},
            { type = "label", id = "bt_info", text = "[INFO]", text_size = 18, background = 0xbb9966, position = {300, 5}, size = {100, 40}, on_click = function()
                game.events:send("encyclopedia.open_item", item)
            end},
        },
        
        on_event =
            function(event, view, data)
                if event == game.events.on_key_press and data == "ESCAPE" then
                    view:setVisible(false)
                    game.ui:clearSelection();
                    item = nil
                end

                if event == game.events.on_deselect then
                    view:setVisible(false)
                    item = nil
                end

                if event == game.events.on_item_selected then
                    item = data;
                    view:setVisible(true)
                    view:findById("lb_name"):setText(item:getLabel())
                    view:findById("lb_position"):setText("Position: " .. item:getX() .. "x" .. item:getY())
                end
            end,
            
        on_refresh =
            function(view)
                if item ~= nil then
                    view:findById("frame_building"):setVisible(not item:isComplete())
                    if not item:isComplete() then
                        view:findById("lb_building_progress"):setText("Progress: " .. item:getCurrentBuild() .. "/" .. item:getTotalBuild())
                        view:findById("lb_building_job"):setText("Build job: " .. (item:getBuildJob() and "yes" or "no"))
                        view:findById("lb_building_character"):setText("Builder: " .. (item:getBuilder() and item:getBuilder():getName() or "no"))
                        view:findById("list_building_components"):getAdapter():setData(item:getComponents());
                    end
                end
            end
    },
}
)