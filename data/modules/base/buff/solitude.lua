data:extend({
    label = "Solitude",
    name = "base.buff_solitude",
    type = "buff",
    on_update = function (data, character)
        if character.needs.relation < 1 then
            return {message = "About to going crazy", level = 3, mood = -15 }
        end

        if character.needs.relation < 50 then
            return {message = "Begins to talk to himself", level = 2, mood = -10 }
        end

        if character.needs.relation < 80 then
            return {message = "Feeling lonely", level = 1, mood = -5 }
        end
    end
})
