_duration = 0;

function OnUpdate(game, character)

    if character.needs.food <= 0 then
        _duration = _duration + 1
    else
        _duration = 0
    end

    if _duration > 52 then
        return {"Is starving to death", 4, -25, {
            {"malnutrition", 1, {4, "extreme"}},
            {"death", 0.05, "Dead from malnutrition"}
        }}
    end

    if _duration > 4800 then
        return {"Is starving to death", 4, -25, {
            {"malnutrition", 1, {3, "severe"}}
        }}
    end

    if _duration > 2400 then
        return {"Is starving to death", 4, -25, {
            {"malnutrition", 1, {2, "moderate"}}
        }}
    end

    if _duration > 1200 then
        return {"Suffering from hunger", 3, -15, {
            {"malnutrition", 1, {1, "slight"}}
        }}
    end

    if _duration > 0 then
        return {"Feeling hungry", 2, -10}
    end

    if character.needs.food < 25 then
        return {"Feeling a little peckish", 1, -5}
    end

end
