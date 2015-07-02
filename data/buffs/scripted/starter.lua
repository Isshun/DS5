function OnCreate(game, character)
end

function OnUpdate(game, character)
    if game.day < 10 then
        return {"Excited by new colony", 1, 15}
    end
end
