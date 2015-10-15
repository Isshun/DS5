data:extend({
    {
        label = "Sleep",
        name = "base.buff_sleep",
        type = "buff",
        on_update = function (data, character)
            if character.needs.isSleeping then
                data.lastItem = character.item
                data.lastItemKnown = true
            end

            if data.lastItemKnown then
                if data.lastItem and data.lastItem.isBed then
                    return {message = "Has slept in a great bed", level = 2, mood = 10 }
                else
                    return {message = "Has slept on the floor", level = 2, mood = -5 }
                end
            end

            -- return {"Has slept well", 2, 5}
        end
    }
})
