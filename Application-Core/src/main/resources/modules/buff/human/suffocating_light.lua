data:extend({
    label = "Suffocating",
    id = "base.buff.suffocating_light",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("oxygen") < 75 and character:getNeeds():get("oxygen") > 25
    end,
    on_update = function (character, data)
        if character:getNeeds():get("oxygen") < 55 then
            return {message = "It's hard to breath (2)", level = 2, mood = -10}
        else
            return {message = "It's hard to breath", level = 1, mood = -5}
        end
    end
})
