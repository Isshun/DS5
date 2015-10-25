data:extend({
    label = "Starter",
    name = "base.buff_starter",
    type = "buff",
    encyclopedia = {
        title = "Excited by new colony",
        content = "The settlers are excited by the challenge of building a new home on this distant planet."
    },
    on_update = function (data, character)
        if game.day < 10 then
            return { message = "Excited by new colony", level = 1, mood = 15 }
        elseif game.day < 20 then
            return { message = "Moderatly excited by new colony", level = 2, mood = 5 }
        end
    end
})
