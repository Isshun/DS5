parcel = nil

data:extend({
    type = "view",
    id = "info_parcel_2",
    controller = "org.smallbox.faraway.core.game.module.world.controller.WorldInfoParcel2Controller",
    position = {0, 100},
    size = {200, 80},
    --background = 0x121c1e,
    level = 100,
    visible = true,
    views = {
        { type = "label", id = "lb_name", text = "name", text_size = 22, size = {100, 28}},
    }
})

data:extend({
    type = "view",
    id = "debug_parcel_info",
    controller = "org.smallbox.faraway.module.dev.controller.info.DebugParcelInfoController",
    position = {0, 200},
    size = {372, 320},
--    background = 0xff1c1e,
    level = 100,
    visible = true,
    views = {
        { type = "list", position = {10, 10}, views = {
            { type = "label", id = "lb_name", text = "name", text_size = 22, size = {100, 28}},
            { type = "label", id = "lb_ground", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_tile", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_position", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_connections", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_type", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_oxygen", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_water", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_light", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_temperature", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_walkable", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_room", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_rock", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_structure", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_consumable", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_item", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_plant", text_size = 14, size = {100, 18}},
            { type = "label", id = "lb_network", text_size = 14, size = {100, 18}},
        }},
    },



    on_event = function(view, event, data)
        if event == application.events.on_parcel_over then
            parcel = data;
        end

        if event == application.events.on_display_change and data[1] == "debug" then
            view:setVisible(data[2])
        end
    end,

    on_refresh = function(view)
        if parcel ~= nil then
            view:findById("lb_position"):setText("Position", ": ", parcel.x .. "x" .. parcel.y .. "x" .. parcel.z)
            view:findById("lb_name"):setText(parcel:getGroundInfo() and parcel:getGroundInfo().label or "no")
            view:findById("lb_ground"):setText("Ground", ": ", parcel:getGroundInfo() and parcel:getGroundInfo().name or "no")
            view:findById("lb_room"):setText("Room", ": ", parcel:getRoom() and parcel:getRoom():getName() or "no")

            view:findById("lb_light"):setText("Light", ": ", parcel:getLight())

            local oxygen = math.round(parcel:getOxygen() * 100)
            view:findById("lb_oxygen"):setText("Oxygen", ": ", oxygen < 0 and "NA" or (oxygen .. "%"))

            view:findById("lb_water"):setText("Water", ": ", (parcel:getLiquidInfo() and (parcel:getLiquidInfo().label .. " (" .. parcel:getLiquidValue() .. ")") or "free"))
            view:findById("lb_temperature"):setText("Temperature", ": ", math.floor(parcel:getTemperature()) .. "°")

            view:findById("lb_walkable"):setText("Walkable", ": ", (parcel:isWalkable() and "yes" or "no"))
            view:findById("lb_tile"):setText("Tile", ": ", parcel:getTile())

            view:findById("lb_rock"):setText("Rock", ": ", parcel:hasRock() and parcel:getRockInfo().name or "no")
            view:findById("lb_structure"):setText("Structure", ": ", parcel:hasStructure() and parcel:getStructureInfo().name or "no")
            view:findById("lb_consumable"):setText("Consumable", ": ", parcel:hasConsumable() and parcel:getConsumable():getInfo().name or "no")
            view:findById("lb_item"):setText("Item", ": ", parcel:hasItem() and parcel:getItem():getInfo().name or "no")
            view:findById("lb_plant"):setText("Plant", ": ", parcel:hasPlant() and parcel:getPlant():getInfo().name or "no")
            view:findById("lb_network"):setText("Network", ": ", "no")

            if parcel:getConnections() then
                local str = "C: "
                local iterator = parcel:getConnections():iterator()
                while iterator:hasNext() do
                    local to_node = iterator:next():getToNode()
                    str = str .. to_node.x .. "x" .. to_node.y .. "x" .. to_node.z .. " "
                end
                view:findById("lb_connections"):setText(str)
            else
                view:findById("lb_connections"):setText("Connections: none")
            end
        end
    end
})