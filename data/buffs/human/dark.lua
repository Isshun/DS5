_duration = 0;

function OnUpdate(game, character)
    if character.character.parcel.light < 0.5 and character.needs.isSleeping then
        _duration = _duration + 1
    else
        _duration = 0
    end

    if _duration > 400 then
        return {message = "In the dark for a long time", level = 1, mood = -5 }
    end
end
