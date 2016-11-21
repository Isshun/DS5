//package org.smallbox.faraway.module.character;
//
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
//import org.smallbox.faraway.core.game.module.character.model.base.CharacterStatsExtra;
//import org.smallbox.faraway.core.data.ItemInfo;
//import org.smallbox.faraway.core.engine.module.GameModule;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Alex on 24/06/2015.
// */
//public class CharacterStuffModule extends GameModule {
//    protected Map<CharacterModel, org.smallbox.faraway.module.character.model.StuffModel> _data;
//
//    @Override
//    protected void onGameStart(Game game) {
//        _data = new HashMap<>();
//
//        addEventListener("base.model.join", new EventListener<CharacterModel>() {
//            @Override
//            public void onEvent(CharacterModel character) {
//
//            }
//        });
//    }
//
//    @Override
//    protected void onGameUpdate(int tick) {
//
//    }
//
//    @Override
//    public void onAddCharacter(CharacterModel character) {
//        // TODO
////        org.smallbox.faraway.module.character.model.StuffModel stuff = new org.smallbox.faraway.module.character.model.StuffModel();
////        stuff.add(Application.data.getEquipment("base.equipments.regular_shirt"));
////        stuff.add(Application.data.getEquipment("base.equipments.regular_pants"));
////        stuff.add(Application.data.getEquipment("base.equipments.regular_shoes"));
////        stuff.add(Application.data.getEquipment("base.equipments.oxygen_bottle"));
////        stuff.add(Application.data.getEquipment("base.equipments.fremen_body"));
////
////        for (ItemInfo item: stuff.getItemsInfo()) {
////            if (item.equipment.effects != null) {
////                for (ItemInfo.EquipmentEffect effect: item.equipment.effects) {
////                    // Check debuff
////                    if (effect.debuff != null) {
////                        addValues(model.getStats().debuff, effect.debuff);
////                    }
////
////                    // Check resist
////                    if (effect.resist != null) {
////                        addValues(model.getStats().resist, effect.resist);
////                    }
////
////                    // Check buff
////                    if (effect.buff != null) {
////                        addValues(model.getStats().buff, effect.buff);
////                    }
////                }
////            }
////        }
////
////        _data.put(model, stuff);
//    }
//
//    private void addValues(CharacterStatsExtra.CharacterStatsValues values, ItemInfo.EquipmentEffectValues effect) {
//        values.coldScore += effect.cold;
//        values.heatScore += effect.heat;
//        values.oxygenScore += effect.oxygen;
//    }
//
//    public ItemInfo getEquipment(CharacterModel character, String location) {
//        for (ItemInfo equipment: _data.get(character).getItemsInfo()) {
//            if (equipment.equipment.location.equals(location)) {
//                return equipment;
//            }
//        }
//        return null;
//    }
//
//}
