data:extend({
    label = "Suffocating",
    id = "base.buff.suffocating",
    type = "buff",
    on_check = function (character, data)
        return character:getNeeds():get("oxygen") <= 25
    end,
    on_update = function (character, data)
            if data.duration.hour >= 1 then
                return {message = "Suffocating (2)", level = 4, mood = -100, {
                    {type = "faint", rate = 0.25},
                    {type = "death", rate = 0.05},
                }}
            else
                return {message = "Suffocating", level = 3, mood = -100}
            end
    end
})
