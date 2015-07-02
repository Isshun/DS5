//package org.smallbox.faraway.game.manager;
//
//import org.smallbox.faraway.game.model.EquipmentModel;
//import org.smallbox.faraway.game.model.GameConfig;
//import org.smallbox.faraway.game.model.character.base.CharacterModel;
//import org.smallbox.faraway.game.model.character.base.CharacterStats;
//
///**
// * Created by Alex on 18/06/2015.
// */
//public class EquipmentManager {
//    private static CharacterStats _stats = new CharacterStats();
//
//    public static void applyEquipment(CharacterModel character) {
//        resetStats();
//
//        for (EquipmentModel equipment: character.getEquipments()) {
//            applyEquipment(equipment);
//        }
//    }
//
//    private static void applyEquipment(EquipmentModel equipment) {
//        for (EquipmentModel.EquipmentEffect buffEffect: equipment.effects) {
//            //if (buffEffect.condition.)
//            _stats.resist.cold += buffEffect.resist.cold;
//        }
//    }
//
//    private static void resetStats() {
//        _stats.resist.cold = 0;
//    }
//}
