_duration = 0;

function OnStart(game, character)
    return character.type == "human" and character.faction == "fremen"
end

function OnUpdate(game, character)
    -- character.stats.buff.oxygen = character.stats.buff.oxygen + 20;
    -- character.stats.resist.hot = character.stats.resist.hot + 20;
end
