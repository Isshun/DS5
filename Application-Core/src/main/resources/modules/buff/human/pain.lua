data:extend({
    {
        label = "Pain",
        id = "base.buff_pain",
        type = "buff",
        on_update = function (character, data)
            if character.needs.pain >= 100 then
                return {message = "Fainted because of the pain", level = 5, mood = -25}
            elseif character.needs.pain >= 75 then
                return {message = "Is in agony", level = 5, mood = -25}
            elseif character.needs.pain >= 50 then
                return {message = "Suffers enormously from woundings", level = 5, mood = -15}
            elseif character.needs.pain >= 25 then
                return {message = "Suffers from woundings", level = 5, mood = -5}
            end
        end
    }
})
