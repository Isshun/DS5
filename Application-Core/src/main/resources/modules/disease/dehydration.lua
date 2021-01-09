data:extend({
    label = "Dehydration",
    id = "base.disease.dehydration",
    type = "disease",
    on_start = function (character, data)
        data.duration = 0
    end,
    on_update = function (character, data)
        return {message = "Dehydration (" .. data[1] .. ")", level = data[1]}
    end
})
