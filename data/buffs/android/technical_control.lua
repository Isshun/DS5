function OnStart(game, character)
    return character.type == 'android' or character.type == 'droid'
end

function OnUpdate(game, character)
    return {message = "Need to initiate maintenance cycle", level = 1, mood = -5 }
end
