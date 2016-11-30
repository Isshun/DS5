ui:extend({
    type = "cursor",
    id = "base.cursor.dig",
    default = { color = 0x88ff0088 },
    odd = { color = 0x33ff8833 },
    eden = { color = 0x3388ff33 },
    pointer = { type = "label", text = "OK", text_size = 34, position = {16, 7}, size = {32, 32} },
    on_parcel = function(parcel)
        return parcel:hasRock()
    end
})