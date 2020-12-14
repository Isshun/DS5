data:extend({
    label = "Hypothermia",
    name = "base.buff.heat",
    type = "buff",
    levels = {
        { message = "Heat 1", effects = {
            {type = "need", name = "drink", value = -0.01},
        }},
        { message = "Heat 2" },
        { message = "Heat 3", effect = {
            { type = "disease", disease = "base.disease.hypothermia", data = {1, "light"}},
        }},
        { message = "Heat 4", effects = {
            {type = "disease", disease = "base.disease.hypothermia", data = {4, "extreme"}},
            {type = "faint", rate = 0.25},
            {type = "death", rate = 0.05},
        }}
    },
    on_get_level = function ()
        return 1
    end,
})
