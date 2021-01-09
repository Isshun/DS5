data:extend({
    {
        label = "Cook easy meal",
        id = "base.receipt_easy_meal",
        type = "receipt",
        cost = 6,
        receipts = {
            {outputs = {{ id = "base.consumable.easy_meal", quantity = 10}}, inputs = {{ id = "base.vegetable", quantity = 10}}},
            {outputs = {{ id = "base.consumable.easy_meal", quantity = 10}}, inputs = {{ id = "base.seaweed", quantity = 10}}},
            {outputs = {{ id = "base.consumable.easy_meal", quantity = 10}}, inputs = {{ id = "base.seafood", quantity = 10}}},
            {outputs = {{ id = "base.consumable.easy_meal", quantity = 10}}, inputs = {{ id = "base.insect_meat", quantity = 100}}},
            {outputs = {{ id = "base.consumable.easy_meal", quantity = 10}}, inputs = {{ id = "base.meat", quantity = 10}}},
        }
    },
    {
        label = "Cook great meal",
        id = "base.receipt_great_meal",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ id = "base.great_meal", quantity = 10}}, inputs = {{ id = "base.vegetable", quantity = 5}, { id = "base.meat", quantity = 5}}},
            {outputs = {{ id = "base.great_meal", quantity = 10}}, inputs = {{ id = "base.vegetable", quantity = 5}, { id = "base.insect_meat", quantity = 50}}},
            {outputs = {{ id = "base.great_meal", quantity = 10}}, inputs = {{ id = "base.seaweed", quantity = 5}, { id = "base.seafood", quantity = 5}}},
            -- Todo: lancer la constuction direct sur la recette dont tout les composants sont disponibles
            -- Si une autre recette devient disponible et que ses composants sont plus proches et que aucun perso n'est sur les composants de la recette actuel, démarrer la nouvelle recette.
        }
    },
    {
        label = "Cook lavish meal",
        id = "base.receipt_lavish_meal",
        type = "receipt",
        cost = 100,
        receipts = {
            {outputs = {{ id = "base.lavish_meal", quantity = {5, 10}}}, inputs = {{ id = "base.vegetable", quantity = 5}, { id = "base.meat", quantity = 5}}},
            {outputs = {{ id = "base.lavish_meal", quantity = {5, 10}}}, inputs = {{ id = "base.vegetable", quantity = 5}, { id = "base.insect_meat", quantity = 50}}},
            {outputs = {{ id = "base.lavish_seafood_meal", quantity = {5, 10}}}, inputs = {{ id = "base.seaweed", quantity = 5}, { id = "base.seafood", quantity = 5}}},
        }
    },
})
