data:extend({
    label = "Dehydration",
    id = "base.buff.dehydration_light",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("drink") > 0 and character:getNeeds():get("drink") < 25
    end,
    on_update = function (character, data)
        return {message = "Is a little thirsty", level = 1, mood = -5}
    end
})