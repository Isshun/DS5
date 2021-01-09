RESOURCES = {
    {"base.calcite_rubble",     4,  50},
    {"base.easy_meal",          4,  15},
    {"base.military_ration",    4,  15},
}

data:extend({
    label = "Crash",
    id = "base.quest.crash",
    type = "quest",

    open_message = "Un cargo de marchandise s'est écrasé à proximité de votre base",
    open_options = {
        "OK",
        "Move to location"
    },

    on_check = function (quest)
        return true
    end,

    on_close = function (quest)
        local location = application.map:getDropLocation()
        local reward = RESOURCES[math.random(#RESOURCES)]
        for i = 1, reward[2] do
            quest.rewards:addConsumable(application.factory:createConsumable(reward[1], reward[3]), location.x, location.y)
        end

        if quest.option == 2 then
            application.camera:move(location.x, location.y)
        end

        return true
    end,
})