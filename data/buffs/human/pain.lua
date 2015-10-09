function OnUpdate(game, character)
    if character.needs.pain >= 100 then
        return {message = "Fainted because of the pain", level = 5, mood = -25}
    end

    if character.needs.pain >= 75 then
        return {message = "Is in agony", level = 5, mood = -25}
    end

    if character.needs.pain >= 50 then
        return {message = "Suffers enormously from woundings", level = 5, mood = -15}
    end

    if character.needs.pain >= 25 then
        return {message = "Suffers from woundings", level = 5, mood = -5}
    end
end
