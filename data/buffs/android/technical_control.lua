function OnStart(game, character)
    return character.type == 'android' or character.type == 'droid'
end

function OnUpdate(game, character)
    return {"Need to initiate maintenance cycle", 1, -5 }
end
