data:extend({
    label = "Hypothermia",
    name = "base.buff_hypotermia",
    type = "buff",
    on_update = function (data, character)
        if character.needs.heat <= 20 then
            return {message = "I'm dying of cold", level = 4, mood = -25, effects = {
                {"faint", 0.25},
                {"death", 0.05},
            }}
        elseif character.needs.heat <= 25 then
            return {message = "I'm frozen", level = 3, mood = -15}
        elseif character.needs.heat <= 32 then
            return {message = "I have lost the feeling in my fingers", level = 2, mood = -7}
        elseif character.needs.heat <= 34 then
            return {message = "I'm cold", level = 1, mood = -5}
        end
    end
})
