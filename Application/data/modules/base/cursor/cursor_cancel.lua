ui:extend({
    type = "cursor",
    id = "base.cursor.cancel",
    default = { color = 0x88ff5555 },
    odd = { color = 0x33999999 },
    eden = { color = 0x33555555 },
    pointer = { type = "label", text = "OK", text_size = 34, position = {16, 7}, size = {32, 32}},
    on_parcel = function(parcel)
        return false
    end
})