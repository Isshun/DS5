data:extend({
    label = "Malnutrition",
    name = "base.disease.malnutrition",
    type = "disease",
    on_start = function (character, data)
        data.duration = 0
    end,
    on_update = function (character, data)
        return {message = "Malnutrition (" .. data[1] .. ")", level = data[1]}
    end
})
