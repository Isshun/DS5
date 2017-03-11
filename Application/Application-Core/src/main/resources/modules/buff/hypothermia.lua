data:extend({
    label = "Hypothermia",
    name = "base.buff.hypothermia",
    class = "org.smallbox.faraway.modules.characterBuff.buffs.HypothermiaBuffHandler",
    type = "buff",
    levels = {
        { message = "I'm cold", mood = -5 },
        { message = "I have lost the feeling in my fingers", mood = -7 },
        { message = "I'm frozen", mood = -15, effect = {
            { type = "disease", disease = "base.disease.hypothermia", data = {1, "light"}},
        }},
        { message = "I'm dying of cold", mood = -25, effects = {
            {type = "disease", disease = "base.disease.hypothermia", data = {4, "extreme"}},
            {type = "faint", rate = 0.25},
            {type = "death", rate = 0.05},
        }}
    },
    on_check = function (character, data)
        return character:getNeeds().heat <= 34
    end,
    on_update = function (character, data)
        local heat = character:getNeeds().heat
        if heat <= 20 then
            return 4
        elseif heat <= 25 then
            return 3
        elseif heat <= 32 then
            return 2
        elseif heat <= 34 then
            return 1
        end
    end
})
