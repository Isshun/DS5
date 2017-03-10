data:extend({
    label = "Environment",
    name = "base.buff.environment_good",
    type = "buff",
    levels = {
        { message = "Pleasant environment", mood = 5 },
        { message = "Really pleasant environment", mood = 10 },
    },
    on_get_level = function (characterModule, weatherModule, game)
        return 1
    end,
})