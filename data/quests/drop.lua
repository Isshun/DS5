function CanBeLaunched(game, quest)
    quest.openMessage = "Un cargo de marchandise s'est écrasé à proximité de votre base"
    quest.openOptions = {
        "OK",
        "Move to location"
    }
    return true
end

function OnLaunch(game, quest)
end

function OnClose(game, quest)
    quest.rewards:addConsumable(game.factory:createConsumable("base.rubble", 1000))

    if quest.option == 2 then
        game.camera:move(5, 5)
    end

    return true
end

function IsOpen(game)
    return false
end
