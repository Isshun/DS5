data:extend({
    type = "cursor",
    id = "base.cursor.gather",
    default = { color = 0x88555588 },
    odd = { color = 0x33999988 },
    eden = { color = 0x33555588 },
    pointer = { type = "label", text = "OK", text_size = 34, position = {16, 7}, size = {32, 32}},
    on_parcel = function(parcel)
        return parcel:getResource() and parcel:getResource():canBeHarvested()
    end
})