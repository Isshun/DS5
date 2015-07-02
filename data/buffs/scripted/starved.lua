_duration = 0;

function OnCreate(game, character)
end

function OnUpdate(game, character)
    _duration = _duration + 1

    if character.needs.food <= 0 and _duration > 64 then
        return {"Is starving to death", 4, -25, {
                {"faint", 0.25},
                {"death", 0.05},
            }}
    end

    if character.needs.food <= 0 and _duration > 32 then
        return {"Suffering from hunger", 3, -15}
    end

    if character.needs.food <= 0 and _duration > 16 then
        return {"Feeling hungry", 2, -10}
    end

    if character.needs.food <= 0 and _duration > 8 then
        return {"Feeling a little peckish", 1, -5}
    end

end
