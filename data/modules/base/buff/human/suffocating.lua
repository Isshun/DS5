data:extend({
    label = "Suffocating",
    name = "base.buff.suffocating",
    type = "buff",
    on_check = function (data, character)
        return character:getNeeds():get("oxygen") <= 25
    end,
    on_update = function (data, character)
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
