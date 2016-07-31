data:extend({
    label = "Entertainment",
    name = "base.buff.entertainemnt_low",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds():get("entertainment") < 20
    end,
    on_update = function (data, character)
        return {message = "Need some entertainment", level = 1, mood = -5 }
    end
})