RESOURCES = {
    {"base.calcite_rubble",     4,  50},
    {"base.easy_meal",          4,  15},
    {"base.military_ration",    4,  15},
}

data:extend({
    label = "Crash",
    name = "base.quest_crash",
    type = "quest",

    on_check = function (quest)
        quest.openMessage = "Un cargo de marchandise s'est écrasé à proximité de votre base"
        quest.openOptions = {
            "OK",
            "Move to location"
        }
        return true
    end,

    on_close = function (quest)
        local location = game.map:getDropLocation()
        local reward = RESOURCES[math.random(#RESOURCES)]
        for i = 1, reward[2] do
            quest.rewards:addConsumable(game.factory:createConsumable(reward[1], reward[3]), location.x, location.y)
        end

        if quest.option == 2 then
            game.camera:move(location.x, location.y)
        end

        return true
    end,
})