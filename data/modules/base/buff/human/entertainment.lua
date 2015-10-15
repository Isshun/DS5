data:extend({
    {
        label = "Entertainment",
        name = "base.buff_entertainemnt",
        type = "buff",
        on_start = function (data, character)
            data.duration = 0
        end,
        on_update = function (data, character)
            if character.needs.joy < 20 then
                return {message = "Need some entertainment", level = 5, mood = -5 }
            elseif character.needs.joy > 80 then
                return {message = "Has a lot of fun", level = 5, mood = 15 }
            end
        end
    }
})