package org.smallbox.faraway.client.ui;

import org.smallbox.faraway.client.render.Viewport;
import org.smallbox.faraway.client.render.layer.BaseLayer;
import org.smallbox.faraway.client.render.layer.GDXRenderer;
import org.smallbox.faraway.client.ui.engine.views.widgets.View;
import org.smallbox.faraway.core.GameLayer;
import org.smallbox.faraway.core.dependencyInjector.annotation.GameObject;
import org.smallbox.faraway.core.dependencyInjector.annotation.Inject;

@GameObject
@GameLayer(level = 1, visible = true)
public class UILayer extends BaseLayer {

    public View _debugView;

    @Inject
    private UIManager uiManager;

    @Override
    protected void onDraw(GDXRenderer renderer, Viewport viewport, double animProgress, int frame) {
        uiManager.draw(renderer, true);

        if (_debugView != null) {
            _debugView.draw(renderer, 0, 0);
        }
    }

//    @Override
//    public void onGameUpdate(Game game) {
//        Globals globals = JsePlatform.standardGlobals();
//        globals.load("function main(a, u, d)\n application = a\n data = d\n ui = u\n math.round = function(num, idp)\n local mult = 10^(idp or 0)\n return math.floor(num * mult + 0.5) / mult\n end end", "main").call();
//        globals.get("main").call(
//                CoerceJavaToLua.coerce(new LuaApplicationModel(null, new LuaEventsModel())),
//                CoerceJavaToLua.coerce(new LuaUIBridge(null) {
//
//                    @Override
//                    public void extend(LuaValue values) {
//                        Log.info("Load lua ui: " + values.get("name").toString());
//                        if (!values.get("type").isnil()) {
//                            RootView rootView = new LuaUIExtend().debug(globals, values);
//                            rootView.getView().setVisible(true);
//                            _debugView = rootView.getView();
//                        }
//                    }
//                }),
//                CoerceJavaToLua.coerce(new LuaDataModel() {
//                    @Override
//                    public void extend(LuaValue values) {
//                    }
//                }));
//
//
//        File f = new File("W:\\projects\\desktop\\FarAway\\Application\\Application-Core\\src\\main\\resources\\modules\\plant\\info_plant.lua");
//        try (FileReader fileReader = new FileReader(f)) {
//            globals.load(fileReader, f.getName()).call();
//        } catch (LuaError | IOException e) {
//            e.printStackTrace();
//        }
//    }
}
