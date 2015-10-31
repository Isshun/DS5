data:extend({
    label = "Starved",
    name = "base.buff.starved",
    type = "buff",
    on_start = function (data, character)
        data.duration = 0
    end,
    on_update = function (data, character)
        if character.needs.food <= 0 then
            data.duration = data.duration + 1
        else
            data.duration = 0
        end

        if data.duration > 5200 then
            return {message = "Is starving to death", level = 4, mood = -25, effects = {
                {"malnutrition", 1, {4, "extreme"}},
                {"death", 0.05, "Dead from malnutrition"}
            }}
        elseif data.duration > 480 then
            return {message = "Is starving to death", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.malnutrition" , data = {3, "severe"}}}}
        elseif data.duration > 240 then
            return {message = "Is starving to death", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.malnutrition" , data = {2, "moderate"}}}}
        elseif data.duration > 120 then
            return {message = "Suffering from hunger", level = 3, mood = -15, effects = {
                {type = "disease", disease = "base.disease.malnutrition" , data = {1, "slight"}}}}
        elseif data.duration > 0 then
            return {message = "Feeling hungry", level = 2, mood = -10}
        elseif character.needs.food < 25 then
            return {message = "Feeling a little peckish", level = 1, mood = -5}
        end
    end
})