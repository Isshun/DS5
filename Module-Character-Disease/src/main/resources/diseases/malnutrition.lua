data:extend({
    label = "Malnutrition",
    name = "base.disease.malnutrition",
    type = "disease",
    on_start = function (data, character)
        data.duration = 0
    end,
    on_update = function (data, character)
        return {message = "Malnutrition (" .. data[1] .. ")", level = data[1]}
    end
})
