data:extend({
    label = "Environment",
    name = "base.buff.environment_bad",
    type = "buff",
    levels = {
        { message = "Unpleasant environment", mood = -5 },
        { message = "Very unpleasant environment", mood = -10 },
        { message = "Extremly unpleasant environment", mood = -15 },
    },
    NOon_get_level = function (character)
        return 1
    end,
})