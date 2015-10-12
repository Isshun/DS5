package org.smallbox.faraway.module.character;

import org.smallbox.faraway.game.model.GameData;
import org.smallbox.faraway.game.model.character.base.CharacterModel;
import org.smallbox.faraway.game.model.character.base.CharacterStats;
import org.smallbox.faraway.game.model.item.ItemInfo;
import org.smallbox.faraway.game.module.GameModule;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 24/06/2015.
 */
public class CharacterStuffModule extends GameModule {
    protected Map<CharacterModel, StuffModel> _data;

    @Override
    protected void onLoaded() {
        _data = new HashMap<>();

        addEventListener("base.character.join", new EventListener<CharacterModel>() {
            @Override
            public void onEvent(CharacterModel character) {

            }
        });
    }

    @Override
    protected void onUpdate(int tick) {

    }

    @Override
    public void onAddCharacter(CharacterModel character) {
        // TODO
//        StuffModel stuff = new StuffModel();
//        stuff.add(GameData.getData().getEquipment("base.equipments.regular_shirt"));
//        stuff.add(GameData.getData().getEquipment("base.equipments.regular_pants"));
//        stuff.add(GameData.getData().getEquipment("base.equipments.regular_shoes"));
//        stuff.add(GameData.getData().getEquipment("base.equipments.oxygen_bottle"));
//        stuff.add(GameData.getData().getEquipment("base.equipments.fremen_body"));
//
//        for (ItemInfo itemInfo: stuff.getItemsInfo()) {
//            if (itemInfo.equipment.effects != null) {
//                for (ItemInfo.EquipmentEffect effect: itemInfo.equipment.effects) {
//                    // Check debuff
//                    if (effect.debuff != null) {
//                        addValues(character.getStats().debuff, effect.debuff);
//                    }
//
//                    // Check resist
//                    if (effect.resist != null) {
//                        addValues(character.getStats().resist, effect.resist);
//                    }
//
//                    // Check buff
//                    if (effect.buff != null) {
//                        addValues(character.getStats().buff, effect.buff);
//                    }
//                }
//            }
//        }
//
//        _data.put(character, stuff);
    }

    private void addValues(CharacterStats.CharacterStatsValues values, ItemInfo.EquipmentEffectValues effect) {
        values.coldScore += effect.cold;
        values.heatScore += effect.heat;
        values.oxygenScore += effect.oxygen;
    }

    public ItemInfo getEquipment(CharacterModel character, String location) {
        for (ItemInfo equipment: _data.get(character).getItemsInfo()) {
            if (equipment.equipment.location.equals(location)) {
                return equipment;
            }
        }
        return null;
    }

}
