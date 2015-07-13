function OnUpdate(game, character)
    if character.needs.heat <= 20 then
        return {"I'm dying of cold", 4, -25, {
            {"faint", 0.25},
            {"death", 0.05},
        }}
    end

    if character.needs.heat <= 25 then
        return {"I'm frozen", 3, -15}
    end

    if character.needs.heat <= 32 then
        return {"I have lost the feeling in my fingers", 2, -7}
    end

    if character.needs.heat <= 34 then
        return {"I'm cold", 1, -5}
    end

end
