function OnUpdate(game, character)
    if character.needs.joy < 20 then
        return {"Need some entertainment", 5, -5 }
    end

    if character.needs.joy > 80 then
        return {"Has a lot of fun", 5, 15 }
    end
end
