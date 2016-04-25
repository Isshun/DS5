data:extend({
    label = "Environment",
    name = "base.buff.environment",
    type = "buff",
    on_check = function (data, character)
        return character:getParcel():getEnvironmentScore() ~= 0
    end,
    on_update = function (data, character)
        local score = character:getParcel():getEnvironmentScore()
        if score <= -50 then
            return {message = "Extremly unpleasant environment", level = 5, mood = -15}
        elseif score <= -25 then
            return {message = "Very unpleasant environment", level = 4, mood = -10}
        elseif score <= -5 then
            return {message = "Unpleasant environment", level = 3, mood = -5}
        elseif score >= 5 then
            return {message = "Pleasant environment", level = 2, mood = 5}
        elseif score >= 15 then
            return {message = "Really pleasant environment", level = 1, mood = 15}
        end
    end
})