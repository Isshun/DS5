structure = nil

data:extend({
    type = "view",
    name = "ui-test",
    position = {1200, 38},
    size = {400, 800},
    background = 0x121c1e,
    visible = false,
    views =
    {
        { type = "label", text = "Structure", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "list", position = {0, 60}, views = {
            { type = "label", id = "lb_durability", text = "lb_durability", text_size = 16, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_walkable", text = "lb_walkable", text_size = 16, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_complete", text = "lb_complete", text_size = 16, position = {10, 10}, size = {-1, 32}},
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

    on_load =
    function(view)
        mode = 3
    end,

    on_event =
    function(view, event, data)
        if event == game.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            game.ui:clearSelection();
            structure = nil
        end

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
            view:findById("lb_walkable"):setText("Walkable: " .. (structure:getInfo().isWalkable and "yes" or "no"))
            view:findById("lb_complete"):setText("Complete: " .. (structure:isComplete() and "yes" or "no"))

            view:findById("frame_building"):setVisible(not structure:isComplete())
            if not structure:isComplete() then
                view:findById("lb_building_progress"):setText("Progress: " .. structure:getCurrentBuild() .. "/" .. structure:getTotalBuild())
                view:findById("lb_building_job"):setText("Build job: " .. (structure:getBuildJob() and "yes" or "no"))
                view:findById("lb_building_character"):setText("Builder: " .. (structure:getBuilder() and structure:getBuilder():getName() or "no"))
                view:findById("lb_building_all_componment"):setText("All components: " .. (structure:hasAllComponents() and "yes" or "no"))
                view:findById("list_building_components"):getAdapter():setData(structure:getComponents());
            end

        end
    end
})