data:extend({
    label = "Hungry",
    name = "base.buff.hungry",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("food") == 0
    end,
    on_update = function (character, data)
        if data.duration.hour > 96 then
            return {message = "Is starving to death", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.malnutrition", data = {4, "extreme"}},
                {type = "death", rate = 0.05, data = "Dead from malnutrition"},
            }}
        elseif data.duration.hour > 72 then
            return {message = "Is starving to death", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.malnutrition" , data = {3, "severe"}}}}
        elseif data.duration.hour > 48 then
            return {message = "Is starving to death", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.malnutrition" , data = {2, "moderate"}}}}
        elseif data.duration.hour > 24 then
            return {message = "Suffering from hunger", level = 3, mood = -15, effects = {
                {type = "disease", disease = "base.disease.malnutrition" , data = {1, "slight"}}}}
        else
            return {message = "Feeling hungry", level = 2, mood = -10}
        end
    end
})