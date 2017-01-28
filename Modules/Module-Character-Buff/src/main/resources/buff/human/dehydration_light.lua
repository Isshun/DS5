data:extend({
    label = "Dehydration",
    name = "base.buff.dehydration_light",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds():get("drink") > 0 and character:getNeeds():get("drink") < 25
    end,
    on_update = function (data, character)
        return {message = "Is a little thirsty", level = 1, mood = -5}
    end
})