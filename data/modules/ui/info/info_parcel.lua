local parcel

data:extend({
    type = "view",
    name = "info_parcel",
    position = {372, 80},
    align = {"bottom", "right"},
    size = {372, 100},
    background = 0x121c1e,
    level = 100,
    visible = false,
    views = {
        { type = "label", id = "lb_ground", text_size = 14},

        { type = "grid", columns = 10, column_width = 58, row_height = 100, position = {10, 6}, views = {
            { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                { type = "image", id = "thumb_o2", src = "[base]/graphics/icons/thumb_o2.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                { type = "label", id = "lb_oxygen", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
            }},

            { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                { type = "image", id = "thumb_walkable", src = "[base]/graphics/icons/thumb_walkable.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                { type = "label", id = "lb_walkable", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
            }},

            { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                { type = "image", id = "thumb_water", src = "[base]/graphics/icons/thumb_water.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                { type = "label", id = "lb_water", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
            }},

            { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                { type = "image", id = "thumb_temperature", src = "[base]/graphics/icons/thumb_temperature.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                { type = "label", id = "lb_temperature", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
            }},

            { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                { type = "image", id = "thumb_temperature", src = "[base]/graphics/icons/thumb_light.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                { type = "label", id = "lb_light", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
            }},

            { type = "view", size = {50, 66}, background = 0x424c4e, views = {
                { type = "image", id = "thumb_inside", src = "[base]/graphics/icons/thumb_home.png", size = {48, 64}, position = {1, 1}, background = 0xb3d035},
                { type = "label", id = "lb_inside", text_size = 14, position = {10, 46}, text_color = 0xb3d035},
            }},
        }}
    },

    on_event = function(view, event, data)
        if event == application.events.on_parcel_over then
            parcel = data;
            view:setVisible(true)
        end
    end,

    on_refresh =
    function(view)
        if parcel ~= nil then
            local room = parcel:getRoom()
            view:findById("lb_ground"):setText("Ground", ": ", parcel:getGroundInfo() and parcel:getGroundInfo().name or "no")

            view:findById("lb_light"):setText(parcel:getLight())

            local oxygen = math.round(parcel:getOxygen() * 100)
            view:findById("lb_oxygen"):setPadding(0, 0, 0, oxygen < 100 and 8 or 0)
            view:findById("lb_oxygen"):setText(oxygen < 0 and "NA" or (oxygen .. "%"))
            view:findById("lb_oxygen"):setTextColor(oxygen < 50 and 0xfe5555 or 0xb3d035)
            view:findById("thumb_o2"):setBackgroundColor(oxygen < 50 and 0xfe5555 or 0xb3d035)

            view:findById("lb_water"):setText("free")
            view:findById("lb_temperature"):setText(math.floor(parcel:getTemperature()) .. "°")

            --            view:findById("lb_room"):setText("Room", ": ", (room and (room:isExterior() and "exterior" or room:getType():name()) or "no"))

            view:findById("lb_walkable"):setText((parcel:isWalkable() and "yes" or "no"))

            view:findById("thumb_inside"):setBackgroundColor(room and room:isExterior() and 0xbbbbbb or 0xb3d035)
            view:findById("lb_inside"):setTextColor(room and room:isExterior() and 0xbbbbbb or 0xb3d035)
            view:findById("lb_inside"):setText((room and (room:isExterior() and "exterior" or room:getType():name()) or "inside"))
        end
    end
})