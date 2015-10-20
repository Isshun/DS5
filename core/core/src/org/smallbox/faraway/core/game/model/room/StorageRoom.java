//package org.smallbox.faraway.game.model.room;
//
//import org.smallbox.faraway.ui.engine.OnClickListener;
//import org.smallbox.faraway.ui.engine.views.View;
//import org.smallbox.faraway.util.Constant;
//import org.smallbox.faraway.util.Log;
//import org.smallbox.faraway.util.StringUtils;
//import ResourceModule;
//import org.smallbox.faraway.game.model.item.*;
//import org.smallbox.faraway.game.model.room.RoomOptions.RoomOption;
//import org.smallbox.faraway.engine.renderer.MainRenderer;
//import org.smallbox.faraway.core.SpriteManager;
//import org.smallbox.faraway.ui.UserInterface;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class StorageRoom extends Room {
//    protected List<UserItem>    _inventory;
//    private List<ItemInfo>        _accepts;
//    private boolean             _acceptFood;
//    private boolean             _acceptDrink;
//    private boolean             _acceptConsomable;
//    private boolean             _acceptGarbage;
//    private RoomOptions            _options;
//    private boolean             _invalidate;
//    private int                 _nbItem;
//    private int                 _inventorySize;
//
//    public StorageRoom() {
//        super(Type.STORAGE);
//        init();
//    }
//
//    public StorageRoom(int id) {
//        super(id, Type.STORAGE);
//        init();
//    }
//
//    private void init() {
//        _inventory = new ArrayList<UserItem>();
//
//        _acceptConsomable = true;
//        _acceptDrink = true;
//        _acceptFood = true;
//        _acceptGarbage = true;
//
//        //        if (info.actions != null && info.actions.storage > 0) {
//        //            _accepts = new ArrayList<ItemInfo>();
//        //            for (ItemInfo itemProduce: info.actions.itemsProduce) {
//        //                _accepts.addAll(itemProduce.craftedFromItems);
//        //            }
//        //        }
//
//    }
//
//    public int getNbItems() {
//        return _inventory.size();
//    }
//
//    public boolean contains(ItemFilter filter) {
//        for (ItemBase item: _inventory) {
//            if (item.matchFilter(filter)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean contains(ItemInfo info) {
//        for (ItemBase item: _inventory) {
//            if (item.getInfo() == info) {
//                return true;
//            }
//        }
//        return false;
//    }
//
////    public List<UserItem> getComponents() {
////        return _inventory;
////    }
//
//    public void setStorageFilter(boolean acceptFood, boolean acceptDrink, boolean acceptConsomable, boolean acceptGarbage) {
//        _acceptFood = acceptFood;
//        _acceptDrink = acceptDrink;
//        _acceptConsomable = acceptConsomable;
//        _acceptGarbage = acceptGarbage;
//    }
//
//    public boolean acceptFood() { return _acceptFood; }
//    public boolean acceptDrink() { return _acceptDrink; }
//    public boolean acceptConsomable() { return _acceptConsomable; }
//    public boolean acceptGarbage() { return _acceptGarbage; }
//
//    public boolean accept(ItemBase item) {
//        if (item == null) {
//            return false;
//        }
//
//        if (!_acceptFood && item.isFood()) {return false; }
//        if (!_acceptDrink && item.isDrink()) {return false; }
//        if (!_acceptConsomable && item.isConsumable()) {return false; }
//        if (!_acceptGarbage && item.isGarbage()) {return false; }
//        if (_accepts != null && !_accepts.needRefresh() && !_accepts.contains(item.getInfo())) { return false; }
//
//        return true;
//    }
//
//    @Override
//    public void addParcel(WorldArea area) {
//        super.addParcel(area);
//        area.setStorage(true);
//        _invalidate = true;
//        _inventorySize++;
//    }
//
//    @Override
//    public void removeArea(WorldArea area) {
//        super.removeArea(area);
//        area.setStorage(false);
//        _invalidate = true;
//        _inventorySize--;
//    }
//
//    @Override
//    public RoomOptions getOptions() {
//        if (_invalidate) {
//            _invalidate = false;
//            Map<ItemInfo, Integer> itemInfos = new HashMap<ItemInfo, Integer>();
//            _options = new RoomOptions();
//            _options.title = StringUtils.getDashedString("Storage", _nbItem + "/" + _inventorySize, Constant.NB_COLUMNS_TITLE);
//            for (UserItem item: _inventory) {
//                int quantity = 1;
//                ItemInfo info = item.getInfo();
//                if (item.isStack()) {
//                    quantity = ((StackItem)item).size();
//                    info = ((StackItem)item).getStackedInfo();
//                }
//                if (info != null) {
//                    itemInfos.put(info, itemInfos.containsKey(info) ? itemInfos.getRoom(info) + quantity : quantity);
//                }
//            }
//            for (ItemInfo info: itemInfos.keySet()) {
//                final ItemInfo finalInfo = info;
//                String str = StringUtils.getDashedString(info.label, String.valueOf(itemInfos.getRoom(info)), Constant.NB_COLUMNS - 4);
//                _options.options.add(new RoomOption(str, SpriteManager.getInstance().getIconDrawable(info), new OnClickListener() {
//                    @Override
//                    public void onClickListener(View view) {
//                        UserInterface.getInstance().select(finalInfo);
//                    }
//                }));
//            }
//        }
//        return _options;
//    }
//
//    /**
//     * Store a collection of item and return accepted item quantity
//     *
//     * @param items
//     * @return number of item stored
//     */
//    public int store(List<UserItem> items) {
//        int quantity = 0;
//        for (UserItem item: items) {
//            if (store(item) == false) {
//                return quantity;
//            }
//            quantity++;
//        }
//        return quantity;
//    }
//
//    /**
//     * Store an item and return true if accepted
//     *
//     * @param item
//     * @return true if success, false if item is not accepted or storage is full
//     */
//    public boolean store(UserItem item) {
//        // Item is a stack
//        if (item.isStack()) {
//            if (_nbItem < _inventorySize) {
//                _nbItem++;
//                _invalidate = true;
//                _inventory.add(item);
//                addItemOnFirstArea(item);
//                ResourceModule.getInstance().add(item.getInfo());
//                MainRenderer.getInstance().invalidate(item.getX(), item.getY());
//                return true;
//            }
//            return false;
//        }
//
//        // Stack already exists
//        for (UserItem inventoryItem: _inventory) {
//            if (inventoryItem.isStack()) {
//                StackItem stack = (StackItem)inventoryItem;
//                if (stack.contains(item.getInfo()) && stack.hasSpaceLeft()) {
//                    stack.add(item);
//                    _invalidate = true;
//                    ResourceModule.getInstance().add(item.getInfo());
//                    MainRenderer.getInstance().invalidate(item.getX(), item.getY());
//                    return true;
//                }
//            }
//        }
//
//        // Create new stack
//        if (_nbItem < _inventorySize) {
//            _nbItem++;
//            StackItem stack = new StackItem();
//            stack.add(item);
//            _inventory.add(stack);
//            _invalidate = true;
//            addItemOnFirstArea(stack);
//            ResourceModule.getInstance().add(item.getInfo());
//            MainRenderer.getInstance().invalidate(item.getX(), item.getY());
//            return true;
//        }
//
//        return false;
//    }
//
//    private void addItemOnFirstArea(UserItem item) {
//        for (WorldArea area: _parcels) {
//            if (area.getItem() == null) {
//                area.setItem(item);
//                break;
//            }
//        }
//    }
//
//    public UserItem take(UserItem item) {
//        UserItem returnItem = null;
//        if (item.isStack()) {
//            returnItem = takeItemInStack((StackItem)item);
//        } else {
//            returnItem = takeItemOnFloor(item);
//        }
//
//        if (returnItem != null) {
//            MainRenderer.getInstance().invalidate(returnItem.getX(), returnItem.getY());
//        }
//
//        return returnItem;
//    }
//
//    private UserItem takeItemOnFloor(UserItem item) {
//        _inventory.remove(item);
//
//        // Remove item from area
//        for (WorldArea area: _parcels) {
//            if (area.getItem() == item) {
//                area.setItem(null);
//            }
//        }
//        _invalidate = true;
//
//        return item;
//    }
//
//    private UserItem takeItemInStack(StackItem stack) {
//        if (stack.size() == 0) {
//            Log.error("stack is empty");
//            return null;
//        }
//
//        UserItem item = stack.take();
//        _invalidate = true;
//
//        // Remove stack if now empty
//        if (stack.size() == 0) {
//            _inventory.remove(item);
//            for (WorldArea area: _parcels) {
//                if (area.getItem() == stack) {
//                    area.setItem(null);
//                }
//            }
//        }
//
//        return item;
//    }
//
//    public UserItem take(ItemFilter filter) {
//        for (UserItem item: _inventory) {
//            if (item.matchFilter(filter)) {
//                return take(item);
//            }
//        }
//        return null;
//    }
//
//    public UserItem take(ItemInfo info) {
//        for (UserItem item: _inventory) {
//            if (item.getInfo() == info) {
//                return take(item);
//            }
//        }
//        return null;
//    }
//}
