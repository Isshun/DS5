function OnUpdate(game, character)
    if character.needs.pain >= 100 then
        return {"Fainted because of the pain", 5, -25}
    end

    if character.needs.pain >= 75 then
        return {"Is in agony", 5, -25}
    end

    if character.needs.pain >= 50 then
        return {"Suffers enormously from woundings", 5, -15}
    end

    if character.needs.pain >= 25 then
        return {"Suffers from woundings", 5, -5}
    end
end
