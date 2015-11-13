//package org.smallbox.faraway.module.extra;
//
//import org.smallbox.faraway.core.game.Game;
//import org.smallbox.faraway.core.game.module.world.model.NetworkObjectModel;
//import org.smallbox.faraway.core.game.module.world.model.item.ItemModel;
//import org.smallbox.faraway.core.game.module.world.model.resource.ResourceModel;
//import org.smallbox.faraway.core.module.GameModule;
//
///**
// * Created by Alex on 05/07/2015.
// */
//public class JNIModule extends GameModule {
//    private JNIBridge _jniBridge;
//
//    @Override
//    protected void onCreate() {
//        _jniBridge = new JNIBridge();
//        _jniBridge.onAddResource();
//    }
//
//    @Override
//    protected void onLoaded(Game game) {
//    }
//
//    @Override
//    protected boolean loadOnStart() {
//        return true;
//    }
//
//    @Override
//    protected void onUpdate(int tick) {
//    }
//
//    @Override
//    public void onAddResource(ResourceModel resource) {
//        _jniBridge.onAddResource();
//    }
//
//    @Override
//    public void onRemovePlant(ResourceModel resource) {
//    }
//
//    @Override
//    public void onAddItem(ItemModel item) {
//    }
//
//    @Override
//    public void onRemoveItem(ItemModel item) {
//    }
//
//    @Override
//    public void onAddNetworkObject(NetworkObjectModel networkObject) {
//    }
//
//    @Override
//    public void onRemoveNetworkObject(NetworkObjectModel networkObject) {
//    }
//}