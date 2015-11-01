data:extend({
    label = "Dark",
    name = "base.buff.dark",
    type = "buff",
    on_check = function (data, character)
        return character:getParcel():getLight() < 0.5
    end,
    on_update = function (data, character)
        if data.duration.hour > 16 then
            return {message = "In the dark for a long time", level = 1, mood = -5 }
        end
    end
})
