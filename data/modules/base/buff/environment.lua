data:extend({
    label = "Environment",
    name = "base.buff_environment",
    type = "buff",
    on_update = function (data, character)
        if character.needs.environment <= -50 then
            return {message = "Extremly unpleasant environment", level = 5, mood = -15}
        elseif character.needs.environment <= -25 then
            return {message = "Very unpleasant environment", level = 4, mood = -10}
        elseif character.needs.environment <= -5 then
            return {message = "Unpleasant environment", level = 3, mood = -5}
        elseif character.needs.environment >= 5 then
            return {message = "Pleasant environment", level = 2, mood = 5}
        elseif character.needs.environment >= 15 then
            return {message = "Really pleasant environment", level = 1, mood = 15}
        end
    end
})
