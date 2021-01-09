data:extend({
    label = "Dehydration",
    id = "base.buff.dehydration",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("drink") == 0
    end,
    on_update = function (character, data)
        if data.duration.hour > 72 then
            return {message = "Dying of thirst", level = 6, mood = -25, effects = {
                {type = "disease", disease = "base.disease.dehydration", data = {4, "extreme"}},
                {type = "death", rate = 0.05, data = "Dead from dehydratation"},
            }}
        elseif data.duration.hour > 48 then
            return {message = "Dying of thirst", level = 5, mood = -25, effects = {
                {type = "disease", disease = "base.disease.dehydration" , data = {3, "severe"}}}}
        elseif data.duration.hour > 24 then
            return {message = "Dying of thirst", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.dehydration" , data = {2, "moderate"}}}}
        elseif data.duration.hour > 12 then
            return {message = "Dying of thirst", level = 3, mood = -15, effects = {
                {type = "disease", disease = "base.disease.dehydration" , data = {1, "slight"}}}}
        else
            return {message = "Is very thirsty", level = 2, mood = -10}
        end
    end
})