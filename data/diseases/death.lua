function OnStart(game, character, data)
    character.stats.isAlive = false
    character.stats.deathMessage = data
end

function OnUpdate(game, character, data)
    return data
end
