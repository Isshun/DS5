data:extend({
    label = "Human",
    name = "base.character.human",
    type = "character",
    needs = {
        energy = {
            warning = 0.50,
            critical = 0.25,
            change = { work = -0.075, rest = -0.05, sleep = 0.2 },
        },
        food = {
            warning = 0.50,
            critical = 0.20,
            change = { work = -0.1, rest = -0.075, sleep = -0.025 },
        },
        water = {
            warning = 0.75,
            critical = 0.50,
            change = { work = -0.1, rest = -0.075, sleep = -0.025 },
        },
        joy = {
            warning = 0.75,
            critical = 0.50,
            change = { work = -0.1, rest = -0.1, sleep = -0.05 },
        },
        relation = {
            warning = 0.75,
            critical = 0.50,
            change = { work = -0.1, rest = -0.1, sleep = -0.05 },
        },
        oxygen = {
            warning = 0.75,
            critical = 0.50,
            change = { work = -0.1, rest = -0.1, sleep = -0.05 },
        },
    },
})