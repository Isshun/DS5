data:extend({
    label = "Starter",
    name = "base.buff.starter",
    type = "buff",
    encyclopedia = {
        title = "Excited by new colony",
        content = "The settlers are excited by the challenge of building a new home on this distant planet."
    },
    levels = {
        { message = "Moderatly excited by new colony", mood = 5 },
        { message = "Excited by new colony", mood = 15 },
    },
    on_get_level = function (game)
        if game:getTime():getDay() < 10 then return 2 end
        if game:getTime():getDay() < 20 then return 1 end
        return 0
    end,
})
