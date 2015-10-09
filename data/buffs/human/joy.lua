function OnUpdate(game, character)
    if character.needs.joy < 20 then
        return {message = "Need some entertainment", level = 5, mood = -5 }
    end

    if character.needs.joy > 80 then
        return {message = "Has a lot of fun", level = 5, mood = 15 }
    end
end
