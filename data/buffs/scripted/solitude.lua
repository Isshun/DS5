function OnUpdate(game, character)
    if character.needs.relation <= 0 then
        return {"About to going crazy", 3, -15 }
    end

    if character.needs.relation < 50 then
        return {"Begins to talk to himself", 2, -10 }
    end

    if character.needs.relation < 80 then
        return {"Feeling lonely", 1, -5 }
    end
end
