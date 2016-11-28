bridge.extend(new org.smallbox.faraway.core.game.modelInfo.ItemInfo(
        label: 'Cooker',
        name : 'base.cooker',
        type: "item",
        category: "kitchen",
//        graphics: new org.smallbox.faraway.core.game.modelInfo.GraphicInfo(packageName: 'base', path: "[base]/graphics/items/workshop/cooker.png"),
        size: [3, 1],
        health: 100,
        slots: [[1, 1]],
        cost: 42,
        factory: new org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoFactory(
                inputSlots: [0, 0],
                outputSlots: [2, 0],
                receipts: [
                        new org.smallbox.faraway.core.game.modelInfo.ItemInfo.FactoryGroupReceiptInfo(receiptName: 'base.receipt_easy_meal'),
                        new org.smallbox.faraway.core.game.modelInfo.ItemInfo.FactoryGroupReceiptInfo(receiptName: 'base.receipt_great_meal'),
                        new org.smallbox.faraway.core.game.modelInfo.ItemInfo.FactoryGroupReceiptInfo(receiptName: 'base.receipt_lavish_meal'),
                ]),
        effects: [
                new org.smallbox.faraway.core.game.modelInfo.ItemInfo.ItemInfoEffects(heat: 50, heatPotency: 40, cold: -50, coldPotency: 40)
        ]),
)

//data:extend({
//    {
//        label = "Cooker",
//        name = "base.cooker",
//        type = "item",
//        category = "kitchen",
//        graphics = { path = "[base]/graphics/items/workshop/cooker.png" },
//        size = {3, 1},
//        health = 100,
//        slots = {{1, 1}},
//        cost = 42,
//        factory = {
//            slots = {
//                inputs = {0, 0},
//                outputs = {2, 0},
//            },
//            receipts = {
//                {receipt = "base.receipt_easy_meal"},
//                {receipt = "base.receipt_great_meal"},
//                {receipt = "base.receipt_lavish_meal"},
//            },
//        },
//        effects = {
//            heat = 50,
//            heatPotency = 40,
//            cold = -50,
//            coldPotency = 40,
//        },
//    }
//})
//
//class Greeter {
//    String sayHello() {
//        greet = "Hello, world!"
//        greet
//    }
//}
//
//def sayHello() {
//    println "Hello, world!"
//
//    bridge.sayHello()
//}