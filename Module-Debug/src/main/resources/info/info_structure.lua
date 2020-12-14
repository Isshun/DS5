structure = nil

ui:extend({
    type = "view",
    id = "base.ui.info_structure",
    style = "base.style.right_panel",
    group = "base.style.right_panel",
    controller = "org.smallbox.faraway.module.structure.StructureInfoController",
    visible = false,
    views = {
        { type = "label", text = "Structure", text_size = 12, position = {10, 8}},
        { type = "view", size = {380, 1}, background = 0xbbbbbb, position = {10, 22}},
        { type = "label", id = "lb_name", text = "name", text_size = 28, position = {0, 24}, padding = 10, size = {100, 40}},
        { type = "view", position = {286, 30}, size = {80, 25}, background = 0x3e4b0b, views = {
            { type = "view", id = "progress_health", size = {50, 25}, background = 0x89ab00 },
            { type = "label", id = "lb_health", text = "80/120", text_size = 16, padding = 7 },
        }},
        { type = "list", position = {0, 60}, views = {
            { type = "label", id = "lb_durability", text = "lb_durability", text_size = 16, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_walkable", text = "lb_walkable", text_size = 16, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_complete", text = "lb_complete", text_size = 16, position = {10, 10}, size = {-1, 32}},
            { type = "label", id = "lb_permeability", text = "lb_permeability", text_size = 16, position = {10, 10}, size = {-1, 32}},
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
            { type = "label", id = "bt_dump", text = "Dump", background = {regular = 0x349394, focus = 0x25c9cb}, text_size = 16, padding = 10, position = {10, 380}, size = {380, 32}, on_click = function()
                application:destroy(structure)
            end},
        }},
    },

    on_event = function(view, event, data)
        if event == application.events.on_key_press and data == "ESCAPE" then
            view:setVisible(false)
            application.game:clearSelection();
            structure = nil
        end

        if event == application.events.on_deselect then
            view:setVisible(false)
            structure = nil
        end

        if event == application.events.on_structure_selected then
            view:setVisible(true)
            structure = data;

            view:findById("lb_name"):setText(structure:getLabel())
        end
    end,

    on_refresh = function(view)
        if structure ~= nil then
            view:findById("lb_durability"):setText(structure:getHealth() .. "/" .. structure:getMaxHealth())
            view:findById("lb_walkable"):setText("Walkable: " .. (structure:getInfo().isWalkable and "yes" or "no"))
            view:findById("lb_complete"):setText("Complete: " .. (structure:isComplete() and "yes" or "no"))
            view:findById("lb_permeability"):setText("Permeability: " .. structure:getInfo().permeability)
            view:findById("lb_health"):setText(structure:getHealth() .. "/" .. structure:getMaxHealth())
            view:findById("progress_health"):setSize(structure:getHealth() / structure:getMaxHealth() * 80, 25)

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