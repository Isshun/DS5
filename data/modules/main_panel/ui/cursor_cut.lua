data:extend({
    {
        type = "cursor",
        id = "base.cut_cursor",
        default = { color = 0x88ff0088 },
        odd = { color = 0x33ff8833 },
        eden = { color = 0x3388ff33 },
        pointer = { type = "label", text = "OK", text_size = 34, position = {16, 7}, size = {32, 32} },
        on_item = function(parcel)
            local actions = parcel:getResource() and parcel:getResource():getInfo().actions or nil
            return actions ~= nil and not actions:isEmpty() and actions:get(0).type == "cut"
        end
    },
})