data:extend({
    label = "Human",
    name = "base.character.human",
    type = "character",
    needs = {
        energy = {
            warning = 35,
            critical = 10,
            change = { work = -1, rest = -1, sleep = 2 },
        },
        food = {
            warning = 75,
            critical = 50,
            change = { work = -1, rest = -1, sleep = -0.5 },
        },
        water = {
            warning = 75,
            critical = 50,
            change = { work = -1, rest = -1, sleep = -0.5 },
        },
        joy = {
            warning = 75,
            critical = 50,
            change = { work = -1, rest = -1, sleep = 1 },
        },
        relation = {
            warning = 75,
            critical = 50,
            change = { work = -1, rest = -1, sleep = 0 },
        },
        oxygen = {
            warning = 75,
            critical = 50,
            change = { work = -1, rest = -1, sleep = -1 },
        },
    },
})