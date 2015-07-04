function OnUpdate(game, character)
    if character.type == 'android' or character.type == 'droid' then
        return {"Need to initiate maintenance cycle", 1, -5 }
    end
end
