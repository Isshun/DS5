RESOURCES = {
    "base.rubble",
    "base.easy_meal",
    "base.military_ration"
}

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
    location = game.map:getDropLocation()
    reward = RESOURCES[math.random(#RESOURCES)]
    quest.rewards:addConsumable(game.factory:createConsumable(reward, 10), location.x, location.y)
    quest.rewards:addConsumable(game.factory:createConsumable(reward, 10), location.x, location.y)
    quest.rewards:addConsumable(game.factory:createConsumable(reward, 10), location.x, location.y)
    quest.rewards:addConsumable(game.factory:createConsumable(reward, 10), location.x, location.y)

    if quest.option == 2 then
        game.camera:move(location.x, location.y)
    end

    return true
end

function IsOpen(game)
    return false
end
