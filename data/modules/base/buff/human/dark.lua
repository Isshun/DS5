data:extend({
    label = "Dark",
    name = "base.buff_dark",
    type = "buff",
    on_update = function (buff_data, character)
        if character.character.parcel and character.character.parcel.light < 0.5 and character.needs.isSleeping then
            buff_data.duration = buff_data.duration + 1
        else
            buff_data.duration = 0
        end

        if buff_data.duration > 400 then
            return {message = "In the dark for a long time", level = 1, mood = -5 }
        end
    end
})
