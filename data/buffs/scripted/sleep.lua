_lastItemKnown = false
_lastItem = nil

function OnUpdate(game, character)
    if character.needs.isSleeping then
        _lastItem = character.item
        _lastItemKnown = true
    end

    if _lastItemKnown then

        if _lastItem and _lastItem.isBed then
            return {"Has slept in a great bed", 2, 10 }
        else
            return {"Has slept on the floor", 2, -5 }
        end

    end

    -- return {"Has slept well", 2, 5}
end
