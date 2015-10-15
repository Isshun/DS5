data:extend({
    {
        label = "Suffocating",
        name = "base.buff_suffocating",
        type = "buff",
        on_start = function (data, character)
            data.duration = 0
        end,
        on_update = function (data, character)
            if character.type == "human" then
                if character.needs.oxygen <= 25 and data.duration > 10 then
                    data.duration = data.duration + 1
                    return {message = "Suffocating (2)", level = 4, mood = -100, {
                        {"faint", 0.25},
                        {"death", 0.05},
                    }}
                elseif character.needs.oxygen <= 25 then
                    data.duration = data.duration + 1
                    return {message = "Suffocating", level = 3, mood = -100}
                elseif character.needs.oxygen < 55 then
                    data.duration = 0
                    return {message = "It's hard to breath (2)", level = 2, mood = -10}
                elseif character.needs.oxygen < 75 then
                    data.duration = 0
                    return {message = "It's hard to breath", level = 1, mood = -5}
                else
                    data.duration = 0
                end
            end
        end
    }
})
