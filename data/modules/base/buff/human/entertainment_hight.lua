data:extend({
    label = "Entertainment",
    name = "base.buff.entertainemnt_hight",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds():get("entertainment") >= 80
    end,
    on_update = function (data, character)
        return {message = "Has a lot of fun", level = 1, mood = 15 }
    end
})