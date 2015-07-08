_duration = 0;

function OnStart(game, character)
    return character.type == "human"
end

function OnUpdate(game, character)
    if character.needs.oxygen <= 25 and _duration > 10 then
        _duration = _duration + 1
        return {"Suffocating (2)", 4, -100, {
            {"faint", 0.25},
            {"death", 0.05},
        }}
    end

    if character.needs.oxygen <= 25 then
        _duration = _duration + 1
        return {"Suffocating", 3, -100}
    end

    if character.needs.oxygen < 55 then
        _duration = 0
        return {"It's hard to breath (2)", 2, -10}
    end

    if character.needs.oxygen < 75 then
        _duration = 0
        return {"It's hard to breath", 1, -5}
    end

end
