game.data:extend({
    {
        label = "Cook easy meal",
        name = "base.receipt_easy_meal",
        type = "receipt",
        products = {
            {items = {{ name = "base.easy_meal", quantity = 10}}, components = {{ name = "base.vegetable", quantity = 5}}},
            {items = {{ name = "base.easy_meal", quantity = 10}}, components = {{ name = "base.seaweed", quantity = 5}}},
            {items = {{ name = "base.easy_meal", quantity = 10}}, components = {{ name = "base.seafood", quantity = 5}}},
            {items = {{ name = "base.easy_meal", quantity = 10}}, components = {{ name = "base.insect_meat", quantity = 50}}},
            {items = {{ name = "base.easy_meal", quantity = 10}}, components = {{ name = "base.base", quantity = 5}}},
        }
    },
    {
        label = "Cook great meal",
        name = "base.receipt_great_meal",
        type = "receipt",
        products = {
            {items = {{ name = "base.great_meal", quantity = 10}}, components = {{ name = "base.vegetable", quantity = 5}, { name = "base.meat", quantity = 5}}},
            {items = {{ name = "base.great_meal", quantity = 10}}, components = {{ name = "base.vegetable", quantity = 5}, { name = "base.insect_meat", quantity = 50}}},
            {items = {{ name = "base.great_meal", quantity = 10}}, components = {{ name = "base.seaweed", quantity = 5}, { name = "base.seafood", quantity = 5}}},
        }
    },
    {
        label = "Cook lavish meal",
        name = "base.receipt_lavish_meal",
        type = "receipt",
        products = {
            {items = {{ name = "base.lavish_meal", quantity = {5, 10}}}, components = {{ name = "base.vegetable", quantity = 5}, { name = "base.meat", quantity = 5}}},
            {items = {{ name = "base.lavish_meal", quantity = {5, 10}}}, components = {{ name = "base.vegetable", quantity = 5}, { name = "base.insect_meat", quantity = 50}}},
            {items = {{ name = "base.lavish_seafood_meal", quantity = {5, 10}}}, components = {{ name = "base.seaweed", quantity = 5}, { name = "base.seafood", quantity = 5}}},
        }
    },
})
