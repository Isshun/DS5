_duration = 0;

function OnUpdate(game, character)

    if character.needs.food <= 0 then
        _duration = _duration + 1
    else
        _duration = 0
    end

    if _duration > 5200 then
        return {message = "Is starving to death", level = 4, mood = -25, effects = {
            {"malnutrition", 1, {4, "extreme"}},
            {"death", 0.05, "Dead from malnutrition"}
        }}
    end

    if _duration > 4800 then
        return {message = "Is starving to death", level = 4, mood = -25, effects = {
            {"malnutrition", 1, {3, "severe"}}
        }}
    end

    if _duration > 2400 then
        return {message = "Is starving to death", level = 4, mood = -25, effects = {
            {"malnutrition", 1, {2, "moderate"}}
        }}
    end

    if _duration > 1200 then
        return {message = "Suffering from hunger", level = 3, mood = -15, effects = {
            {"malnutrition", 1, {1, "slight"}}
        }}
    end

    if _duration > 0 then
        return {message = "Feeling hungry", level = 2, mood = -10}
    end

    if character.needs.food < 25 then
        return {message = "Feeling a little peckish", level = 1, mood = -5}
    end

end
