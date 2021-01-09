data:extend({
    label = "Entertainment",
    id = "base.buff.entertainemnt_low",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("entertainment") < 20
    end,
    on_update = function (character, data)
        return {message = "Need some entertainment", level = 1, mood = -5 }
    end
})