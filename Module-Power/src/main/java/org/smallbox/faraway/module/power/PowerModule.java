//package org.smallbox.faraway.module.power;
//
//import org.smallbox.faraway.core.engine.module.GameModule;
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.world.model.ParcelModel;
//import UsableItem;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * Created by Alex
// */
//public class PowerModule extends GameModule {
//    private static final int    UPDATE_INTERVAL = 40;
//
//    private List<UsableItem>     _items;
//    private double              _produce;
//    private double              _stored;
//    private double              _maxStorage;
//
//    @Override
//    protected void onGameStart(Game game) {
//        _stored = 1000;
//        _maxStorage = 3500;
//        _items = new ArrayList<>();
//    }
//
//    @Override
//    protected void onGameUpdate(Game game, int tick) {
//        if (tick % UPDATE_INTERVAL == 0) {
//            Collections.shuffle(_items);
//            double powerLeft = _stored;
//            for (UsableItem item : _items) {
//                if (powerLeft + item.getInfo().power > 0) {
//                    powerLeft += item.getInfo().power;
//                    item.setFunctional(true);
//                } else {
//                    item.setFunctional(false);
//                }
//            }
//            _produce = powerLeft - _stored;
//            _stored = Math.min(_maxStorage, powerLeft);
//        }
//    }
//
//    @Override
//    public void onAddItem(UsableItem item){
//        if (item != null) {
//            if (item.getInfo().power != 0) {
//                _items.add(item);
//            }
//        }
//    }
//
//    @Override
//    public void onRemoveItem(ParcelModel parcel, UsableItem item){
//        _items.remove(item);
//    }
//
//    public double getProduce() {
//        return _produce;
//    }
//
//    public double getStored() {
//        return _stored;
//    }
//}
