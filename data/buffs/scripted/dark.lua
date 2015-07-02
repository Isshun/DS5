_duration = 0;

function OnUpdate(game, character)
    if character.needs.light < 50 then
        _duration = _duration + 1;

        if _duration > 8 then
            return {"In the dark for a long time", 1, -5 }
        end
    end
end
