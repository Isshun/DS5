data:extend({
    label = "Hungry",
    name = "base.buff.hungry_light",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds():get("food") > 0 and character:getNeeds():get("food") < 25
    end,
    on_update = function (data, character)
        return {message = "Feeling a little peckish", level = 1, mood = -5}
    end
})