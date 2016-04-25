data:extend({
    {
        label = "Cook easy meal",
        name = "base.receipt_easy_meal",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ name = "base.easy_meal", quantity = 10}}, inputs = {{ name = "base.vegetable", quantity = 10}}},
            {outputs = {{ name = "base.easy_meal", quantity = 10}}, inputs = {{ name = "base.seaweed", quantity = 10}}},
            {outputs = {{ name = "base.easy_meal", quantity = 10}}, inputs = {{ name = "base.seafood", quantity = 10}}},
            {outputs = {{ name = "base.easy_meal", quantity = 10}}, inputs = {{ name = "base.insect_meat", quantity = 100}}},
            {outputs = {{ name = "base.easy_meal", quantity = 10}}, inputs = {{ name = "base.meat", quantity = 10}}},
        }
    },
    {
        label = "Cook great meal",
        name = "base.receipt_great_meal",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ name = "base.great_meal", quantity = 10}}, inputs = {{ name = "base.vegetable", quantity = 5}, { name = "base.meat", quantity = 5}}},
            {outputs = {{ name = "base.great_meal", quantity = 10}}, inputs = {{ name = "base.vegetable", quantity = 5}, { name = "base.insect_meat", quantity = 50}}},
            {outputs = {{ name = "base.great_meal", quantity = 10}}, inputs = {{ name = "base.seaweed", quantity = 5}, { name = "base.seafood", quantity = 5}}},
            -- Todo: lancer la constuction direct sur la recette dont tout les composants sont disponibles
            -- Si une autre recette devient disponible et que ses composants sont plus proches et que aucun perso n'est sur les composants de la recette actuel, démarrer la nouvelle recette.
        }
    },
    {
        label = "Cook lavish meal",
        name = "base.receipt_lavish_meal",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ name = "base.lavish_meal", quantity = {5, 10}}}, inputs = {{ name = "base.vegetable", quantity = 5}, { name = "base.meat", quantity = 5}}},
            {outputs = {{ name = "base.lavish_meal", quantity = {5, 10}}}, inputs = {{ name = "base.vegetable", quantity = 5}, { name = "base.insect_meat", quantity = 50}}},
            {outputs = {{ name = "base.lavish_seafood_meal", quantity = {5, 10}}}, inputs = {{ name = "base.seaweed", quantity = 5}, { name = "base.seafood", quantity = 5}}},
        }
    },
})
