data:extend({
    label = "Entertainment",
    id = "base.buff.entertainemnt_hight",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("entertainment") >= 80
    end,
    on_update = function (character, data)
        return {message = "Has a lot of fun", level = 1, mood = 15 }
    end
})