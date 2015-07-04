_duration = 0;

function OnUpdate(game, character)
    if character.needs.oxygen <= 20 and _duration > 10 then
        _duration = _duration + 1
        return {"Suffocating (2)", 4, -100, {
            {"faint", 0.25},
            {"death", 0.05},
        }}
    end

    if character.needs.oxygen <= 20 then
        _duration = _duration + 1
        return {"Suffocating", 3, -100}
    end

    if character.needs.oxygen < 50 then
        _duration = 0
        return {"It's hard to breath (2)", 2, -25}
    end

    if character.needs.oxygen < 75 then
        _duration = 0
        return {"It's hard to breath", 1, -10}
    end

end
