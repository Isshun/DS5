function OnUpdate(game, character)
    if character.needs.environment <= -50 then
        return {"Extremly unpleasant environment", 5, -15}
    end

    if character.needs.environment <= -25 then
        return {"Very unpleasant environment", 4, -10}
    end

    if character.needs.environment <= -5 then
        return {"Unpleasant environment", 3, -5}
    end

    if character.needs.environment >= 5 then
        return {"Pleasant environment", 2, 5}
    end

    if character.needs.environment >= 15 then
        return {"Really pleasant environment", 1, 15}
    end

end
