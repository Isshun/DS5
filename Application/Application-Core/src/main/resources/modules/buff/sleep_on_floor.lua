data:extend({
    label = "Sleep",
    name = "base.buff.sleep_on_floor",
    type = "buff",
    duration = 12 * 50,
    on_check = function (character, data)
        return character:isSleeping() and character:getJob() and not character:getJob():getItem()
    end,
    on_update = function (character, data)
        return {message = "Has slept on the floor", level = 2, mood = -5 }
    end
})