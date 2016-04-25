data:extend({
    {
        label = "Health",
        name = "base.buff_health",
        type = "buff",
        on_start = function (data, character)
            return character.type == 'android' or character.type == 'droid'
        end,
        on_update = function (data, character)
            return {message = "Need to initiate maintenance cycle", level = 1, mood = -5 }
        end
    }
})
