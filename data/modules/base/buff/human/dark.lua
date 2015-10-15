data:extend({
    {
        label = "Dark",
        name = "base.buff_dark",
        type = "buff",
        on_update = function (data, character)
            if character.character.parcel.light < 0.5 and character.needs.isSleeping then
                data.duration = data.duration + 1
            else
                data.duration = 0
            end

            if data.duration > 400 then
                return {message = "In the dark for a long time", level = 1, mood = -5 }
            end
        end
    }
})
