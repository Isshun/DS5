data:extend({
    label = "Sleep",
    name = "base.buff.sleep_in_bed",
    type = "buff",
    duration = 12 * 50,
    on_check = function (data, character)
        return character:isSleeping() and character:getJob() and character:getJob():getItem()
    end,
    on_update = function (data, character)
        return {message = "Has slept in a great bed", level = 2, mood = 10 }
    end
})