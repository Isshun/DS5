data:extend({
    label = "Malnutrition",
    name = "base.disease.malnutrition",
    type = "disease",
    on_start = function (data, character)
        data.duration = 0
    end,
    on_update = function (data, character)
        return "Malnutrition (" .. data[2] .. ")"
    end
})
