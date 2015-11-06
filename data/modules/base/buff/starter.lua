data:extend({
    label = "Starter",
    name = "base.buff.starter",
    type = "buff",
    encyclopedia = {
        title = "Excited by new colony",
        content = "The settlers are excited by the challenge of building a new home on this distant planet."
    },
    on_check = function (data, character)
        return application.day < 20
    end,
    on_update = function (data, character)
        if application.day < 10 then
            return { message = "Excited by new colony", level = 1, mood = 15 }
        elseif application.day < 20 then
            return { message = "Moderatly excited by new colony", level = 2, mood = 5 }
        end
    end
})
