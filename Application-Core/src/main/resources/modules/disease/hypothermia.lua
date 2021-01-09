data:extend({
    label = "Hypothermia",
    id = "base.disease.hypothermia",
    type = "disease",
    on_start = function (character, data)
        data.duration = 0
    end,
    on_update = function (character, data)
        return {message = "Hypothermia (" .. data[1] .. ")", level = data[1]}
    end
})
