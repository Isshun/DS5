data:extend({
    label = "Hypothermia",
    name = "base.buff.hypothermia",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds().heat <= 34
    end,
    on_update = function (data, character)
        local heat = character:getNeeds().heat
        if heat <= 20 then
            return {message = "I'm dying of cold", level = 4, mood = -25, effects = {
                {type = "disease", disease = "base.disease.hypothermia", data = {4, "extreme"}},
                {type = "faint", rate = 0.25},
                {type = "death", rate = 0.05},
            }}
        elseif heat <= 25 then
            return {message = "I'm frozen", level = 3, mood = -15, effect = {
                { type = "disease", disease = "base.disease.hypothermia", data = {1, "light"}},
            }}
        elseif heat <= 32 then
            return {message = "I have lost the feeling in my fingers", level = 2, mood = -7}
        elseif heat <= 34 then
            return {message = "I'm cold", level = 1, mood = -5}
        end
    end
})
