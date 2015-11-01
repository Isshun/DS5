data:extend({
    label = "Suffocating",
    name = "base.buff.suffocating_light",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds():get("oxygen") < 75
    end,
    on_update = function (data, character)
        if character:getNeeds():get("oxygen") < 55 then
            return {message = "It's hard to breath (2)", level = 2, mood = -10}
        else
            return {message = "It's hard to breath", level = 1, mood = -5}
        end
    end
})
