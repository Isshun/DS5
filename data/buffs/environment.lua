function OnUpdate(game, character)
    if character.needs.environment <= -50 then
        return {message = "Extremly unpleasant environment", level = 5, mood = -15}
    end

    if character.needs.environment <= -25 then
        return {message = "Very unpleasant environment", level = 4, mood = -10}
    end

    if character.needs.environment <= -5 then
        return {message = "Unpleasant environment", level = 3, mood = -5}
    end

    if character.needs.environment >= 5 then
        return {message = "Pleasant environment", level = 2, mood = 5}
    end

    if character.needs.environment >= 15 then
        return {message = "Really pleasant environment", level = 1, mood = 15}
    end

end
