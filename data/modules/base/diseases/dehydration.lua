data:extend({
    label = "Dehydration",
    name = "base.disease.dehydration",
    type = "disease",
    on_start = function (data, character)
        data.duration = 0
    end,
    on_update = function (data, character)
        return {message = "Dehydration (" .. data[1] .. ")", level = data[1]}
    end
})
