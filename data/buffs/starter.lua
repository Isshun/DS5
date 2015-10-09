function OnCreate(game, character)
end

function OnUpdate(g, character)
    if g.day < 10 then
        return {
            message = "Excited by new colony",
            level = 1,
            mood = 15,
            on_click = function()
                game.events:send("encyclopedia.open", {"Excited by new colony", "The settlers are excited by the challenge of building a new home on this distant planet."})
            end
        }
    end
end
