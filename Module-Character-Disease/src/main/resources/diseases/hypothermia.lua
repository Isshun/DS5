data:extend({
    label = "Hypothermia",
    name = "base.disease.hypothermia",
    type = "disease",
    on_start = function (data, character)
        data.duration = 0
    end,
    on_update = function (data, character)
        return {message = "Hypothermia (" .. data[1] .. ")", level = data[1]}
    end
})
