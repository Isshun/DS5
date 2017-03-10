data:extend({
    {
        label = "Health",
        name = "base.buff_health",
        type = "buff",
        on_start = function (character, data)
            return character.type == 'android' or character.type == 'droid'
        end,
        on_update = function (character, data)
            return {message = "Need to initiate maintenance cycle", level = 1, mood = -5 }
        end
    }
})
