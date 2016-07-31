//package org.smallbox.faraway.module.consumable;
//
//import org.smallbox.faraway.core.game.module.character.model.base.CharacterModel;
//import org.smallbox.faraway.core.game.module.world.model.ConsumableModel;
//import org.smallbox.faraway.core.game.module.world.model.ParcelModel;
//import org.smallbox.faraway.core.util.MoveListener;
//
///**
// * Created by Alex on 29/07/2016.
// */
//public class OnReachConsumableListener implements MoveListener<CharacterModel> {
//    private final ConsumableModule  _consumableModule;
//    private final CharacterModel    _character;
//    private final ConsumableModel   _consumable;
//    private final int               _quantity;
//    private final ParcelModel       _targetParcel;
//
//    public OnReachConsumableListener(ConsumableModule consumableModule, ConsumableModel consumable, int quantity, ParcelModel targetParcel, CharacterModel character) {
//        _consumableModule = consumableModule;
//        _character = character;
//        _consumable = consumable;
//        _quantity = quantity;
//        _targetParcel = targetParcel;
//    }
//
//    @Override
//    public void onReach(CharacterModel movable) {
//        if (_consumable.getQuantity() <= _quantity) {
//            _consumable.setParcel(null);
//            _character.addInventory(_consumable, _consumable.getQuantity());
//        } else {
//            _character.addInventory(_consumableModule.create(_consumable.getInfo(), _quantity), _quantity);
//        }
//
//        _character.moveTo(_targetParcel, );
//    }
//
//    @Override
//    public void onFail(CharacterModel movable) {
//        _consumable.setJob(null);
//        quit(character);
//    }
//}
