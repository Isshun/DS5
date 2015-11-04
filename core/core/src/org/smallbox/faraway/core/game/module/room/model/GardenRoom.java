//package org.smallbox.faraway.game.model.room;
//
//import org.smallbox.faraway.core.SpriteManager;
//import org.smallbox.faraway.engine.renderer.MainRenderer;
//import org.smallbox.faraway.game.Game;
//import org.smallbox.faraway.game.model.Data;
//import org.smallbox.faraway.game.model.item.ItemInfo;
//import org.smallbox.faraway.game.model.item.ParcelModel;
//import org.smallbox.faraway.game.model.item.ResourceModel;
//
//import java.util.List;
//
//public class GardenRoom extends RoomModel {
//    private enum State {
//        RAW, GROWING, MATURE
//    }
//
//    private static final double GROW_VALUE = 0.1;
//    private ItemInfo             _currentCulture;
//    private List<ItemInfo>         _cultures;
//    private RoomOptions            _options;
//    private State                 _state;
//
//    public GardenRoom() {
//        super(RoomType.GARDEN);
//        init();
//    }
//
//    public GardenRoom(int id) {
//        super(id, RoomType.GARDEN);
//        init();
//    }
//
//    private void init() {
//        _options = new RoomOptions();
//        _cultures  = Data.getData().gatherItems;
//        for (ItemInfo c: _cultures) {
//            final ItemInfo culture = c;
//            _options.options.add(new RoomOption("Set " + culture.label,
//                    SpriteManager.getInstance().getIconDrawable(culture),
//                    view -> setCulture(culture)));
//        }
//        _currentCulture = Data.getData().getRandomGatherItem();
//    }
//
//    public void setCulture(ItemInfo culture) {
//        if (culture != _currentCulture) {
//            _state = State.RAW;
//            _currentCulture = culture;
//            for (ParcelModel model: _parcels) {
//                ModuleHelper.getWorldModule().replaceItem(_currentCulture, model.getX(), model.getY(), 0);
//            }
//        }
//    }
//
//    public State getState() { return _state; }
//
//    @Override
//    public void update() {
//        for (ParcelModel model: _parcels) {
//            if (model.getResource() == null) {
//                ModuleHelper.getWorldModule().putObject(_currentCulture, model.getX(), model.getY(), 0, 0);
//            }
//
//            ResourceModel res = model.getResource();
//            if (res != null && res.isType(_currentCulture) && res.isMature() == false) {
//                if ((int)res.getQuantity() != (int)(res.getQuantity() + GROW_VALUE)) {
//                    MainRenderer.getInstance().invalidate(res.getX(), res.getY());
//                }
//                res.addQuantity(GROW_VALUE);
//            }
//        }
//    }
//
//    public ItemInfo getCulture() {
//        return _currentCulture;
//    }
//
//    public RoomOptions getOptions() {
//        return _options;
//    }
//}
